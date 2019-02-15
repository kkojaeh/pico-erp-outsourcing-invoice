package pico.erp.outsourcing.invoice;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import pico.erp.audit.AuditApi;
import pico.erp.audit.AuditConfiguration;
import pico.erp.invoice.InvoiceApi;
import pico.erp.item.ItemApi;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceApi.Roles;
import pico.erp.outsourcing.order.OutsourcingOrderApi;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.ApplicationStarter;
import pico.erp.shared.Public;
import pico.erp.shared.SpringBootConfigs;
import pico.erp.shared.data.Role;
import pico.erp.shared.impl.ApplicationImpl;
import pico.erp.user.UserApi;

@Slf4j
@SpringBootConfigs
public class OutsourcingInvoiceApplication implements ApplicationStarter {

  public static final String CONFIG_NAME = "outsourcing-invoice/application";

  public static final Properties DEFAULT_PROPERTIES = new Properties();

  static {
    DEFAULT_PROPERTIES.put("spring.config.name", CONFIG_NAME);
  }

  public static SpringApplication application() {
    return new SpringApplicationBuilder(OutsourcingInvoiceApplication.class)
      .properties(DEFAULT_PROPERTIES)
      .web(false)
      .build();
  }

  public static void main(String[] args) {
    application().run(args);
  }

  @Override
  public Set<ApplicationId> getDependencies() {
    return Stream.of(
      UserApi.ID,
      ItemApi.ID,
      AuditApi.ID,
      OutsourcingOrderApi.ID,
      InvoiceApi.ID
    ).collect(Collectors.toSet());
  }

  @Override
  public ApplicationId getId() {
    return OutsourcingInvoiceApi.ID;
  }

  @Override
  public boolean isWeb() {
    return false;
  }

  @Bean
  @Public
  public Role outsourcingInvoiceManager() {
    return Roles.OUTSOURCING_INVOICE_MANAGER;
  }

  @Bean
  @Public
  public Role outsourcingInvoicePublisher() {
    return Roles.OUTSOURCING_INVOICE_PUBLISHER;
  }


  @Override
  public pico.erp.shared.Application start(String... args) {
    return new ApplicationImpl(application().run(args));
  }

}
