package com.ucan.skawallet.back.end.skawallet.service;

import com.ucan.skawallet.back.end.skawallet.enums.UserType;
import com.ucan.skawallet.back.end.skawallet.enums.WalletType;
import com.ucan.skawallet.back.end.skawallet.enums.PartnerCategory;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionType;
import com.ucan.skawallet.back.end.skawallet.enums.TransactionStatus;
import com.ucan.skawallet.back.end.skawallet.enums.PaymentMethod;
import com.ucan.skawallet.back.end.skawallet.enums.InstallmentStatus;
import com.ucan.skawallet.back.end.skawallet.model.DigitalWallets;
import com.ucan.skawallet.back.end.skawallet.model.Users;
import com.ucan.skawallet.back.end.skawallet.model.Transactions;
import com.ucan.skawallet.back.end.skawallet.model.Installment;
import com.ucan.skawallet.back.end.skawallet.model.Partner;
import com.ucan.skawallet.back.end.skawallet.model.Produto;
import com.ucan.skawallet.back.end.skawallet.repository.DigitalWalletRepository;
import com.ucan.skawallet.back.end.skawallet.repository.UserRepository;
import com.ucan.skawallet.back.end.skawallet.repository.TransactionRepository;
import com.ucan.skawallet.back.end.skawallet.repository.InstallmentRepository;
import com.ucan.skawallet.back.end.skawallet.repository.PartnerRepository;
import com.ucan.skawallet.back.end.skawallet.repository.ProdutoRepository;
import com.ucan.skawallet.back.end.skawallet.service.TopUpReferenceService;
import com.ucan.skawallet.back.end.skawallet.model.TopUpReference;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelUploadService {

    private final UserRepository userRepository;
    private final DigitalWalletRepository digitalWalletRepository;
    private final TransactionRepository transactionRepository;
    private final InstallmentRepository installmentRepository;
    private final PartnerRepository partnerRepository;
    private final ProdutoRepository produtoRepository;
    private final TopUpReferenceService topUpReferenceService;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private static final String EXCEL_FILE_PATH = "Carregar Dados.xlsx";

    @Transactional
    public void simulateTransactions() {
        log.info("Iniciando simulação de transações...");
        List<Users> users = userRepository.findAll();
        List<DigitalWallets> allWallets = digitalWalletRepository.findAll();
        Random random = new Random();

        if (users.isEmpty() || allWallets.isEmpty()) {
            log.warn("Nenhum usuário ou carteira encontrados para simulação.");
            return;
        }

        for (Users user : users) {
            List<DigitalWallets> userWallets = digitalWalletRepository.findByUser(user);
            if (userWallets.isEmpty()) {
                continue;
            }

            log.info("Simulando transações para o usuário: {}", user.getName());

            for (int i = 0; i < 20; i++) {
                try {
                    DigitalWallets sourceWallet = userWallets.get(random.nextInt(userWallets.size()));
                    TransactionType type = getRandomTransactionType(random);
                    BigDecimal amount = BigDecimal.valueOf(random.nextDouble() * 1000 + 10); // 10 to 1010

                    Transactions transaction = new Transactions();
                    transaction.setAmount(amount);
                    transaction.setTransactionType(type);
                    transaction.setStatus(TransactionStatus.COMPLETED);
                    transaction.setCreatedAt(
                            LocalDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24)));
                    transaction.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
                    transaction.setDescription("Simulação: " + type);

                    if (type == TransactionType.DEPOSIT) {
                        transaction.setDestinationWallet(sourceWallet);
                        sourceWallet.setBalance(sourceWallet.getBalance().add(amount));
                        digitalWalletRepository.save(sourceWallet);
                    } else if (type == TransactionType.WITHDRAWAL) {
                        if (sourceWallet.getBalance().compareTo(amount) >= 0) {
                            transaction.setSourceWallet(sourceWallet);
                            sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
                            digitalWalletRepository.save(sourceWallet);
                        } else {
                            // Skip if insufficient funds
                            continue;
                        }
                    } else if (type == TransactionType.TRANSFER || type == TransactionType.PAYMENT) {
                        // Pick a random destination wallet not owned by the same user (if possible)
                        List<DigitalWallets> potentialDestinations = allWallets.stream()
                                .filter(w -> !w.getUser().getPkUsers().equals(user.getPkUsers()))
                                .collect(Collectors.toList());

                        if (potentialDestinations.isEmpty()) {
                            potentialDestinations = allWallets; // Fallback
                        }

                        DigitalWallets destWallet = potentialDestinations
                                .get(random.nextInt(potentialDestinations.size()));

                        if (sourceWallet.getBalance().compareTo(amount) >= 0) {
                            transaction.setSourceWallet(sourceWallet);
                            transaction.setDestinationWallet(destWallet);

                            sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
                            destWallet.setBalance(destWallet.getBalance().add(amount));

                            digitalWalletRepository.save(sourceWallet);
                            digitalWalletRepository.save(destWallet);
                        } else {
                            continue;
                        }
                    }

                    transactionRepository.save(transaction);

                } catch (Exception e) {
                    log.error("Erro ao simular transação para usuário {}: {}", user.getName(), e.getMessage());
                }
            }
        }
        log.info("Simulação de transações concluída.");
    }

    private TransactionType getRandomTransactionType(Random random) {
        TransactionType[] types = { TransactionType.DEPOSIT, TransactionType.WITHDRAWAL, TransactionType.TRANSFER,
                TransactionType.PAYMENT };
        return types[random.nextInt(types.length)];
    }

    @Transactional
    public void simulateInstallments() {
        fixInstallmentSchema();
        log.info("Iniciando simulação de parcelamentos...");
        List<Users> users = userRepository.findAll();
        List<Partner> partners = partnerRepository.findAll();
        List<Produto> products = produtoRepository.findAll();
        Random random = new Random();

        if (users.isEmpty() || partners.isEmpty() || products.isEmpty()) {
            log.warn("Dados insuficientes para simulação (Usuários, Parceiros ou Produtos faltando).");
            return;
        }

        for (Users user : users) {
            // Check if user has a wallet
            List<DigitalWallets> userWallets = digitalWalletRepository.findByUser(user);
            if (userWallets.isEmpty()) {
                continue;
            }

            log.info("Simulando parcelamentos para o usuário: {}", user.getName());

            for (int i = 0; i < 4; i++) {
                try {
                    Partner partner = partners.get(random.nextInt(partners.size()));
                    // Filter products for this partner
                    List<Produto> partnerProducts = products.stream()
                            .filter(p -> p.getPartner().getPkPartners().equals(partner.getPkPartners()))
                            .collect(Collectors.toList());

                    if (partnerProducts.isEmpty()) {
                        continue;
                    }

                    Produto produto = partnerProducts.get(random.nextInt(partnerProducts.size()));

                    BigDecimal price = produto.getPreco();
                    if (price == null) {
                        price = BigDecimal.valueOf(random.nextDouble() * 50000 + 5000); // Random price if null
                    }

                    int installmentsCount = random.nextInt(3) + 3; // 3 to 5 installments
                    BigDecimal monthlyPayment = price.divide(BigDecimal.valueOf(installmentsCount),
                            java.math.RoundingMode.HALF_UP);

                    Installment installment = new Installment();
                    installment.setUser(user);
                    installment.setPartner(partner);
                    installment.setProduto(produto);
                    installment.setTotalAmount(price);
                    installment.setInstallments(installmentsCount);
                    installment.setRemainingInstallments(installmentsCount - 1); // Assume 1st paid
                    installment.setMonthlyPayment(monthlyPayment);
                    installment.setNextDueDate(LocalDate.now().plusMonths(1));
                    installment.setStatus(InstallmentStatus.PENDING);

                    installmentRepository.save(installment);

                    // Create transaction for the first payment
                    DigitalWallets wallet = userWallets.get(0); // Use first wallet
                    if (wallet.getBalance().compareTo(monthlyPayment) >= 0) {
                        wallet.setBalance(wallet.getBalance().subtract(monthlyPayment));
                        digitalWalletRepository.save(wallet);

                        Transactions tx = new Transactions();
                        tx.setAmount(monthlyPayment);
                        tx.setTransactionType(TransactionType.INSTALLMENT_PAYMENT);
                        tx.setStatus(TransactionStatus.COMPLETED);
                        tx.setCreatedAt(LocalDateTime.now());
                        tx.setSourceWallet(wallet);
                        tx.setPaymentMethod(PaymentMethod.DIGITAL_WALLET);
                        tx.setDescription("Pagamento inicial parcelamento: " + produto.getNome());
                        transactionRepository.save(tx);
                    }

                } catch (Exception e) {
                    log.error("Erro ao simular parcelamento para usuário {}: {}", user.getName(), e.getMessage());
                }
            }
        }
        log.info("Simulação de parcelamentos concluída.");
    }

    @Transactional
    public void simulateTopUps() {
        log.info("Iniciando simulação de recargas (TopUps)...");
        List<Users> users = userRepository.findAll();
        Random random = new Random();

        if (users.isEmpty()) {
            log.warn("Nenhum usuário encontrado para simulação de recargas.");
            return;
        }

        for (Users user : users) {
            List<DigitalWallets> userWallets = digitalWalletRepository.findByUser(user);
            if (userWallets.isEmpty()) {
                continue;
            }

            log.info("Simulando recargas para o usuário: {}", user.getName());

            // Simulate 2-5 top-ups per user
            int topUpCount = random.nextInt(4) + 2;

            for (int i = 0; i < topUpCount; i++) {
                try {
                    DigitalWallets wallet = userWallets.get(random.nextInt(userWallets.size()));
                    BigDecimal amount = BigDecimal.valueOf(random.nextDouble() * 5000 + 500); // 500 to 5500

                    // Generate reference
                    TopUpReference reference = topUpReferenceService.generateReference(wallet.getWalletCode(), amount);

                    // Confirm reference (simulate payment)
                    topUpReferenceService.confirmTopUp(reference.getReferenceCode());

                    log.info("Recarga efetuada: {} - Valor: {}", reference.getReferenceCode(), amount);

                } catch (Exception e) {
                    log.error("Erro ao simular recarga para usuário {}: {}", user.getName(), e.getMessage());
                }
            }
        }
        log.info("Simulação de recargas concluída.");
    }

    private void fixInstallmentSchema() {
        try {
            log.info("Tentando corrigir constraint de chave estrangeira em installments...");
            entityManager
                    .createNativeQuery("ALTER TABLE installments DROP CONSTRAINT IF EXISTS fk6pwphdwn7gtkdq9ls4n7rvhb9")
                    .executeUpdate();
            // Check if correct constraint exists or just add it (might fail if exists, so
            // maybe check first or catch exception)
            // Simpler: Drop if exists (we know the wrong one name), then add the correct
            // one if not exists?
            // Or just try to add and ignore error?
            // Let's try to add it. If it fails, it might already exist.
            try {
                entityManager.createNativeQuery(
                        "ALTER TABLE installments ADD CONSTRAINT fk_installments_produto FOREIGN KEY (fk_product) REFERENCES produto(id)")
                        .executeUpdate();
                log.info("Constraint fk_installments_produto adicionada com sucesso.");
            } catch (Exception e) {
                log.warn("Constraint fk_installments_produto já deve existir ou erro ao adicionar: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Erro ao corrigir schema: {}", e.getMessage());
        }
    }

    @Transactional
    public void loadData() {
        try (FileInputStream file = new FileInputStream(new File(EXCEL_FILE_PATH));
                Workbook workbook = new XSSFWorkbook(file)) {

            // Map to store Users by Row Number (Excel ID)
            Map<String, Users> userMap = new java.util.HashMap<>();

            // Load Users from "users" sheet
            Sheet userSheet = workbook.getSheet("users");
            if (userSheet != null) {
                log.info("Processing Sheet: {}", userSheet.getSheetName());
                Iterator<Row> userRowIterator = userSheet.iterator();

                if (userRowIterator.hasNext()) {
                    userRowIterator.next(); // Skip header
                }

                while (userRowIterator.hasNext()) {
                    Row row = userRowIterator.next();
                    try {
                        Users user = parseUser(row);
                        if (user != null) {
                            Users finalUser = user;
                            boolean exists = false;

                            // Check if user exists in DB (Email)
                            Optional<Users> existingByEmail = userRepository.findByEmail(user.getEmail());
                            if (existingByEmail.isPresent()) {
                                log.info("Usuário com email {} já existe. Usando existente.", user.getEmail());
                                finalUser = existingByEmail.get();
                                exists = true;
                            }

                            // Check if user exists in DB (Phone)
                            if (!exists && user.getPhone() != null) {
                                Optional<Users> existingByPhone = userRepository.findByPhone(user.getPhone());
                                if (existingByPhone.isPresent()) {
                                    log.info("Usuário com telefone {} já existe. Usando existente.", user.getPhone());
                                    finalUser = existingByPhone.get();
                                    exists = true;
                                }
                            }

                            if (!exists) {
                                finalUser = userRepository.save(user);
                                log.info("Usuário {} salvo com ID: {}", finalUser.getEmail(), finalUser.getPkUsers());
                            }

                            // Map by Row Number (Implicit ID used in Wallet sheet)
                            String rowKey = String.valueOf(row.getRowNum());
                            userMap.put(rowKey, finalUser);
                        }
                    } catch (Exception e) {
                        log.error("Erro ao processar linha de usuário {}: {}", row.getRowNum(), e.getMessage());
                    }
                }
            } else {
                log.warn("Aba 'users' não encontrada no arquivo Excel.");
            }

            // Load Wallets from "wallet" sheet
            Sheet walletSheet = workbook.getSheet("wallet");
            if (walletSheet != null) {
                log.info("Processing Sheet: {}", walletSheet.getSheetName());
                Iterator<Row> walletRowIterator = walletSheet.iterator();
                List<DigitalWallets> wallets = new ArrayList<>();

                if (walletRowIterator.hasNext()) {
                    walletRowIterator.next(); // Skip header
                }

                while (walletRowIterator.hasNext()) {
                    Row row = walletRowIterator.next();
                    try {
                        DigitalWallets wallet = parseWallet(row, userMap);
                        if (wallet != null) {
                            // Check for duplicates: User + WalletName
                            List<DigitalWallets> userWallets = digitalWalletRepository.findByUser(wallet.getUser());
                            boolean exists = userWallets.stream()
                                    .anyMatch(w -> w.getWalletName().equalsIgnoreCase(wallet.getWalletName()));

                            if (exists) {
                                log.info("Carteira '{}' para usuário {} já existe. Pulando.", wallet.getWalletName(),
                                        wallet.getUser().getEmail());
                                continue;
                            }

                            // Check for duplicates in current list
                            boolean alreadyInList = wallets.stream()
                                    .anyMatch(w -> w.getUser().getEmail().equals(wallet.getUser().getEmail())
                                            && w.getWalletName().equalsIgnoreCase(wallet.getWalletName()));
                            if (alreadyInList) {
                                log.info("Carteira '{}' para usuário {} duplicada na planilha. Pulando.",
                                        wallet.getWalletName(), wallet.getUser().getEmail());
                                continue;
                            }

                            wallets.add(wallet);
                        }
                    } catch (Exception e) {
                        log.error("Erro ao processar linha de carteira {}: {}", row.getRowNum(), e.getMessage());
                    }
                }

                if (!wallets.isEmpty()) {
                    digitalWalletRepository.saveAll(wallets);
                    log.info("{} carteiras carregadas com sucesso.", wallets.size());
                }
            } else {
                log.warn("Aba 'wallet' não encontrada no arquivo Excel.");
            }

            // Load Partners from "partner" sheet
            Map<String, Partner> partnerMap = new java.util.HashMap<>();
            Sheet partnerSheet = workbook.getSheet("partner");
            if (partnerSheet != null) {
                log.info("Processing Sheet: {}", partnerSheet.getSheetName());
                Iterator<Row> partnerRowIterator = partnerSheet.iterator();
                List<Partner> partners = new ArrayList<>();

                if (partnerRowIterator.hasNext()) {
                    partnerRowIterator.next(); // Skip header
                }

                while (partnerRowIterator.hasNext()) {
                    Row row = partnerRowIterator.next();
                    try {
                        Partner partner = parsePartner(row);
                        if (partner != null) {
                            // Check for duplicates
                            if (partnerRepository.existsByName(partner.getName())) {
                                log.info("Parceiro '{}' já existe. Pulando.", partner.getName());
                                // If exists, we should probably fetch it to put in map?
                                // Or just skip? If we skip, products linking to it will fail.
                                // Let's try to fetch it.
                                Optional<Partner> existing = partnerRepository.findByName(partner.getName());
                                if (existing.isPresent()) {
                                    partner = existing.get();
                                }
                            } else {
                                partners.add(partner);
                            }

                            // Map by Row Number
                            String rowKey = String.valueOf(row.getRowNum());
                            partnerMap.put(rowKey, partner);
                        }
                    } catch (Exception e) {
                        log.error("Erro ao processar linha de parceiro {}: {}", row.getRowNum(), e.getMessage());
                    }
                }

                if (!partners.isEmpty()) {
                    partnerRepository.saveAll(partners);
                    log.info("{} parceiros carregados com sucesso.", partners.size());
                }
            } else {
                log.warn("Aba 'partner' não encontrada no arquivo Excel.");
            }

            // Load Products from "product" sheet
            Sheet productSheet = workbook.getSheet("product");
            if (productSheet == null) {
                productSheet = workbook.getSheet("products"); // Try plural
            }

            if (productSheet != null) {
                log.info("Processing Sheet: {}", productSheet.getSheetName());
                Iterator<Row> productRowIterator = productSheet.iterator();
                List<Produto> products = new ArrayList<>();

                if (productRowIterator.hasNext()) {
                    productRowIterator.next(); // Skip header
                }

                while (productRowIterator.hasNext()) {
                    Row row = productRowIterator.next();
                    try {
                        Produto product = parseProduct(row, partnerMap);
                        if (product != null) {
                            // Check for duplicates (Name + Partner)
                            // Assuming we don't want same product name for same partner
                            // For now just save, or check if needed.
                            // Let's just save for now as duplicates might be allowed or handled by DB
                            // constraints if any
                            products.add(product);
                        }
                    } catch (Exception e) {
                        log.error("Erro ao processar linha de produto {}: {}", row.getRowNum(), e.getMessage());
                    }
                }

                if (!products.isEmpty()) {
                    produtoRepository.saveAll(products);
                    log.info("{} produtos carregados com sucesso.", products.size());
                }
            } else {
                log.warn("Aba 'product' ou 'products' não encontrada no arquivo Excel.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo Excel: " + e.getMessage());
        }
    }

    private Partner parsePartner(Row row) {
        // Col 0: Name
        // Col 1: Description
        // Col 2: Category
        // Col 3: ContactInfo

        String name = getCellValue(row.getCell(0));
        if (name == null || name.isEmpty()) {
            return null;
        }

        String description = getCellValue(row.getCell(1));
        String categoryStr = getCellValue(row.getCell(2));
        String contactInfo = getCellValue(row.getCell(3));

        PartnerCategory category = PartnerCategory.RETAIL; // Default
        if (categoryStr != null) {
            try {
                category = PartnerCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Categoria inválida '{}', usando padrão RETAIL.", categoryStr);
            }
        }

        // Generate a simple code if not provided (assuming not provided in excel)
        String code = name.toUpperCase().replaceAll("\\s+", "_");
        if (code.length() > 10) {
            code = code.substring(0, 10);
        }
        code = code + "_" + new Random().nextInt(1000);

        return Partner.builder()
                .name(name)
                .partnerCode(code)
                .description(description)
                .category(category)
                .contactInfo(contactInfo)
                .paymentSupported(true)
                .build();
    }

    private Produto parseProduct(Row row, Map<String, Partner> partnerMap) {
        // Col 0: Name
        // Col 1: Description
        // Col 2: Price
        // Col 3: Partner Ref (Row Number)

        String name = getCellValue(row.getCell(0));
        String description = getCellValue(row.getCell(1));
        String priceStr = getCellValue(row.getCell(2));
        String partnerRef = getCellValue(row.getCell(3));

        log.info("Parsing Product Row {}: Name='{}', Desc='{}', Price='{}', PartnerRef='{}'",
                row.getRowNum(), name, description, priceStr, partnerRef);

        if (name == null || partnerRef == null) {
            return null;
        }

        BigDecimal price = null;
        if (priceStr != null) {
            try {
                price = new BigDecimal(priceStr.replaceAll("[^\\d.]", ""));
            } catch (NumberFormatException e) {
                log.warn("Preço inválido '{}', definindo como null.", priceStr);
            }
        }

        // Normalize partner ref
        if (partnerRef.endsWith(".0")) {
            partnerRef = partnerRef.substring(0, partnerRef.length() - 2);
        }

        // Find partner by map
        Partner partner = partnerMap.get(partnerRef);
        if (partner == null) {
            log.warn("Parceiro Ref '{}' não encontrado para o produto '{}'. Ignorando.", partnerRef, name);
            return null;
        }

        return Produto.builder()
                .nome(name)
                .descricao(description)
                .preco(price)
                .partner(partner)
                .build();
    }

    /*
     * private Partner parsePartner(Row row) {
     * // Assuming columns: Name, Description, Category, ContactInfo
     * String name = getCellValue(row.getCell(0));
     * if (name == null || name.isEmpty()) {
     * return null;
     * }
     * 
     * String description = getCellValue(row.getCell(1));
     * String categoryStr = getCellValue(row.getCell(2));
     * String contactInfo = getCellValue(row.getCell(3));
     * 
     * PartnerCategory category = PartnerCategory.RETAIL; // Default
     * if (categoryStr != null) {
     * try {
     * category = PartnerCategory.valueOf(categoryStr.toUpperCase());
     * } catch (IllegalArgumentException e) {
     * log.warn("Categoria inválida '{}', usando padrão RETAIL.", categoryStr);
     * }
     * }
     * 
     * return Partner.builder()
     * .name(name)
     * .partnerCode(generatePartnerCode(name))
     * .description(description)
     * .category(category)
     * .contactInfo(contactInfo)
     * .paymentSupported(true)
     * .build();
     * }
     */

    private Users parseUser(Row row) {
        // Mapping:
        // Col 0: Name
        // Col 1: Email
        // Col 2: Password
        // Col 3: Phone
        // Col 4: Type

        String name = getCellValue(row.getCell(0));
        String email = getCellValue(row.getCell(1));
        String password = getCellValue(row.getCell(2));
        String phone = getCellValue(row.getCell(3));
        String typeStr = getCellValue(row.getCell(4));

        if (name == null || email == null) {
            return null;
        }

        // Default password if missing
        if (password == null) {
            password = "password123";
        }

        UserType type = UserType.USER; // Default
        if (typeStr != null) {
            try {
                type = UserType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Tipo de usuário inválido '{}', usando padrão USER.", typeStr);
            }
        }

        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setType(type);
        user.setEnabled(true);
        user.setLocked(false);

        return user;
    }

    private DigitalWallets parseWallet(Row row, Map<String, Users> userMap) {
        // Mapping:
        // Col 0: Wallet Name
        // Col 1: Wallet Type (PERSONAL, MERCHANT, SAVINGS)
        // Col 2: Balance
        // Col 3: Currency
        // Col 4: User Email (to link)
        // Col 5: External User ID (Row Number)

        String walletName = getCellValue(row.getCell(0));
        String typeStr = getCellValue(row.getCell(1));
        String balanceStr = getCellValue(row.getCell(2));
        String currency = getCellValue(row.getCell(3));
        String userEmail = getCellValue(row.getCell(4));
        String externalId = getCellValue(row.getCell(5));

        // Normalize external ID (remove .0)
        if (externalId != null && externalId.endsWith(".0")) {
            externalId = externalId.substring(0, externalId.length() - 2);
        }

        if (walletName == null) {
            return null;
        }

        Users user = null;

        // Try to find by External ID (Row Number) first
        if (externalId != null && userMap.containsKey(externalId)) {
            user = userMap.get(externalId);
        }
        // Fallback to Email if present
        else if (userEmail != null) {
            Optional<Users> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        if (user == null) {
            log.warn("Usuário não encontrado para a carteira {} (ExtID: {}, Email: {}). Ignorando.", walletName,
                    externalId, userEmail);
            return null;
        }

        WalletType type = WalletType.PERSONAL; // Default
        if (typeStr != null) {
            try {
                type = WalletType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Tipo de carteira inválido '{}', usando padrão PERSONAL.", typeStr);
            }
        }

        BigDecimal balance = BigDecimal.ZERO;
        if (balanceStr != null) {
            try {
                balance = new BigDecimal(balanceStr.replaceAll("[^\\d.]", ""));
            } catch (NumberFormatException e) {
                log.warn("Saldo inválido '{}', usando 0.", balanceStr);
            }
        }

        DigitalWallets wallet = new DigitalWallets();
        wallet.setWalletName(walletName);
        wallet.setWalletType(type);
        wallet.setBalance(balance);
        wallet.setUser(user);
        wallet.setCurrency(currency != null ? currency : "AKZ");
        wallet.setIsDefault(false); // Default to false

        return wallet;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
