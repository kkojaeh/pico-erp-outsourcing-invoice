package pico.erp.outsourcing.invoice;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.order.OutsourcingOrderId;

@Repository
public interface OutsourcingInvoiceRepository {

  OutsourcingInvoice create(@NotNull OutsourcingInvoice orderAcceptance);

  void deleteBy(@NotNull OutsourcingInvoiceId id);

  boolean exists(@NotNull OutsourcingInvoiceId id);

  boolean exists(@NotNull InvoiceId invoiceId);

  Optional<OutsourcingInvoice> findBy(@NotNull OutsourcingInvoiceId id);

  Optional<OutsourcingInvoice> findBy(@NotNull InvoiceId invoiceId);

  Stream<OutsourcingInvoice> findAllBy(@NotNull OutsourcingOrderId orderId);

  void update(@NotNull OutsourcingInvoice orderAcceptance);

}
