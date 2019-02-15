package pico.erp.outsourcing.invoice;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.data.Role;

public final class OutsourcingInvoiceApi {

  public final static ApplicationId ID = ApplicationId.from("outsourcing-invoice");

  @RequiredArgsConstructor
  public enum Roles implements Role {

    OUTSOURCING_INVOICE_PUBLISHER,
    OUTSOURCING_INVOICE_MANAGER;

    @Id
    @Getter
    private final String id = name();

  }
}
