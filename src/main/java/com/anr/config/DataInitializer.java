package com.anr.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.anr.localmdb.model.InsuranceMember;
import com.anr.localmdb.model.Plan;
import com.anr.localmdb.model.Policy;
import com.anr.localmdb.model.Product;
import com.anr.localmdb.repository.MemberRepository;
import com.anr.localmdb.repository.ProductRepository;

/**
 * Data Initializer for H2 Database
 * Loads sample products and insurance members on application startup
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepo, MemberRepository memberRepo) {
        return args -> {
            logger.info("Initializing H2 database with sample data...");

            // Initialize Products
            initializeProducts(productRepo);

            // Initialize Insurance Members
            initializeMembers(memberRepo);

            logger.info("Database initialization complete!");
        };
    }

    private void initializeProducts(ProductRepository productRepo) {
        logger.info("Loading sample products...");

        List<Product> products = new ArrayList<>();

        // Scooby Doo series toys
        products.add(createProduct("001", "scooby", "scooby the dog toy from scooby doo series", "$3.50"));
        products.add(createProduct("002", "shaggy", "shaggy toy from scooby doo series", "$3.50"));
        products.add(createProduct("003", "velma", "velma toy from scooby doo series", "$3.50"));
        products.add(createProduct("004", "daphne", "daphne toy from scooby doo series", "$3.50"));
        products.add(createProduct("005", "fred", "fred toy from scooby doo series", "$3.50"));

        // Cars series toys
        products.add(createProduct("006", "lightening mcqueen", "mcqueen toy from cars1 series", "$5.50"));
        products.add(createProduct("007", "doc hudson", "doc hudson toy from cars1 series", "$5.50"));
        products.add(createProduct("008", "sally carrera", "sally toy from cars1 series", "$5.50"));
        products.add(createProduct("009", "mater", "mater toy from cars1 series", "$5.50"));
        products.add(createProduct("010", "strip weathers", "king strip weather toy from cars1 series", "$5.50"));

        productRepo.saveAll(products);
        logger.info("Loaded {} products", products.size());
    }

    private Product createProduct(String id, String name, String description, String price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return product;
    }

    private void initializeMembers(MemberRepository memberRepo) {
        logger.info("Loading sample insurance members...");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Sample Member: John Deer
            InsuranceMember member = new InsuranceMember();
            member.setId("545345");
            member.setPartyId("1234411");
            member.setFirstname("john");
            member.setLastname("deer");
            member.setDateOfBirth(sdf.parse("1984-02-04"));

            // Create policies for John Deer
            List<Policy> policies = new ArrayList<>();

            // Policy 1 - Current year
            Policy policy1 = new Policy();
            policy1.setPolicyID("12345");
            policy1.setHccID("H0129321");
            policy1.setGroup("B0123");
            policy1.setDivision("123");
            policy1.setPolicyStartDate(sdf.parse("2020-01-01"));
            policy1.setPolicyExpiryDate(sdf.parse("2020-12-31"));

            List<Plan> plans1 = new ArrayList<>();
            Plan plan1a = new Plan();
            plan1a.setPlanDefinitionId(111);
            plan1a.setPlanName("happyHealth");
            plan1a.setPlanNumber("01405");
            plan1a.setSegment("small group");
            plan1a.setPlanFamily("ForWorkoutPeople");
            plan1a.setCoverageType("health");
            plan1a.setStatus("active");
            plans1.add(plan1a);

            Plan plan1b = new Plan();
            plan1b.setPlanDefinitionId(321);
            plan1b.setPlanName("basicMedicines");
            plan1b.setPlanNumber("23");
            plan1b.setSegment("small group");
            plan1b.setPlanFamily("ForWorkoutPeople");
            plan1b.setCoverageType("rx");
            plan1b.setStatus("active");
            plans1.add(plan1b);

            policy1.setPlans(plans1);
            policies.add(policy1);

            // Policy 2 - Previous year
            Policy policy2 = new Policy();
            policy2.setPolicyID("12312");
            policy2.setHccID("H1234567");
            policy2.setGroup("B0123");
            policy2.setDivision("123");
            policy2.setPolicyStartDate(sdf.parse("2019-01-01"));
            policy2.setPolicyExpiryDate(sdf.parse("2019-12-31"));

            List<Plan> plans2 = new ArrayList<>();
            Plan plan2a = new Plan();
            plan2a.setPlanDefinitionId(109);
            plan2a.setPlanName("previousHealth");
            plan2a.setPlanNumber("01123");
            plan2a.setSegment("small group");
            plan2a.setPlanFamily("ForWorkoutPeople");
            plan2a.setCoverageType("health");
            plan2a.setStatus("active");
            plans2.add(plan2a);

            Plan plan2b = new Plan();
            plan2b.setPlanDefinitionId(321);
            plan2b.setPlanName("basicMedicines");
            plan2b.setPlanNumber("23");
            plan2b.setSegment("small group");
            plan2b.setPlanFamily("ForWorkoutPeople");
            plan2b.setCoverageType("rx");
            plan2b.setStatus("active");
            plans2.add(plan2b);

            policy2.setPlans(plans2);
            policies.add(policy2);

            // Policy 3 - Older year
            Policy policy3 = new Policy();
            policy3.setPolicyID("12121");
            policy3.setHccID("H0114311");
            policy3.setGroup("B0111");
            policy3.setDivision("222");
            policy3.setPolicyStartDate(sdf.parse("2018-01-01"));
            policy3.setPolicyExpiryDate(sdf.parse("2018-12-31"));

            List<Plan> plans3 = new ArrayList<>();
            Plan plan3a = new Plan();
            plan3a.setPlanDefinitionId(108);
            plan3a.setPlanName("olderHealth");
            plan3a.setPlanNumber("01043");
            plan3a.setSegment("individual under 65");
            plan3a.setPlanFamily("ForNormalPeople");
            plan3a.setCoverageType("health");
            plan3a.setStatus("soft retired");
            plans3.add(plan3a);

            Plan plan3b = new Plan();
            plan3b.setPlanDefinitionId(321);
            plan3b.setPlanName("basicMedicines");
            plan3b.setPlanNumber("23");
            plan3b.setSegment("small group");
            plan3b.setPlanFamily("ForWorkoutPeople");
            plan3b.setCoverageType("rx");
            plan3b.setStatus("active");
            plans3.add(plan3b);

            policy3.setPlans(plans3);
            policies.add(policy3);

            member.setPolicies(policies);
            memberRepo.save(member);

            logger.info("Loaded 1 insurance member with {} policies", policies.size());

        } catch (Exception e) {
            logger.error("Error initializing member data", e);
        }
    }
}
