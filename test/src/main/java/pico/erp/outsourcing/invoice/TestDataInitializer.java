package pico.erp.outsourcing.invoice;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import pico.erp.shared.ApplicationInitializer;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@Profile({"test-data"})
public class TestDataInitializer implements ApplicationInitializer {

  @Lazy
  @Autowired
  private OutsourcingInvoiceService outsourcingInvoiceService;


  @Autowired
  private DataProperties dataProperties;

  @Override
  public void initialize() {
    dataProperties.outsourcingInvoices.forEach(outsourcingInvoiceService::create);
  }

  @Data
  @Configuration
  @ConfigurationProperties("data")
  public static class DataProperties {

    List<OutsourcingInvoiceRequests.CreateRequest> outsourcingInvoices = new LinkedList<>();

  }

}
