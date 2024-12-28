package io.ysalih.mulberryCS;

import io.ysalih.mulberryCS.model.Realestate;
import io.ysalih.mulberryCS.repository.RealestateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MulberryCsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MulberryCsApplication.class, args);
    }


    @Bean
    public CommandLineRunner insertRealEstateData(RealestateRepository repository) {
        return args -> {
            var x = repository.findAll();
            if (x.size() > 0) {
                return;
            }


            List<Realestate> realEstates = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                realEstates.add(new Realestate(
                        "" + i,
                        50000.0 + (i * 1000),
                        (i % 4 == 0 ? "Villa" : i % 4 == 1 ? "Land" : i % 4 == 2 ? "Office" : "Apartment"),
                        50.0 + (i * 10),
                        (i % 8 == 0 ? "Atasehir" : i % 8 == 1 ? "Silivri" : i % 8 == 2 ? "Basaksehir" : i % 8 == 3 ? "Kartal" : i % 8 == 4 ? "Tuzla" : i % 8 == 5 ? "Besiktas" : i % 8 == 6 ? "Sariyer" : "Maslak"),
                        i % 2 == 0 ? "For Sale" : "For Rent",
                        2000 + (i % 24)
                ));
            }

            repository.saveAll(realEstates);
        };
    }
}