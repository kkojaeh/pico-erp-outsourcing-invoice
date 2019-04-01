package pico.erp.outsourcing.invoice;

import kkojaeh.spring.boot.component.Give;
import kkojaeh.spring.boot.component.SpringBootComponent;
import kkojaeh.spring.boot.component.SpringBootComponentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceApi.Roles;
import pico.erp.shared.SharedConfiguration;
import pico.erp.shared.data.Role;

@Slf4j
@SpringBootComponent("outsourcing-invoice")
@EntityScan
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
@SpringBootApplication
@Import(value = {
  SharedConfiguration.class
})
public class OutsourcingInvoiceApplication {

  public static void main(String[] args) {
    new SpringBootComponentBuilder()
      .component(OutsourcingInvoiceApplication.class)
      .run(args);
  }

  @Bean
  @Give
  public Role outsourcingInvoiceManager() {
    return Roles.OUTSOURCING_INVOICE_MANAGER;
  }

  @Bean
  @Give
  public Role outsourcingInvoicePublisher() {
    return Roles.OUTSOURCING_INVOICE_PUBLISHER;
  }

}
