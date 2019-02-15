package pico.erp.outsourcing.invoice;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.order.OutsourcingOrderId;

public interface OutsourcingInvoiceService {

  void cancel(@Valid @NotNull OutsourcingInvoiceRequests.CancelRequest request);

  OutsourcingInvoiceData create(@Valid @NotNull OutsourcingInvoiceRequests.CreateRequest request);

  boolean exists(@Valid @NotNull OutsourcingInvoiceId id);

  boolean exists(@Valid @NotNull InvoiceId invoiceId);

  OutsourcingInvoiceData get(@Valid @NotNull OutsourcingInvoiceId id);

  OutsourcingInvoiceData get(@Valid @NotNull InvoiceId invoiceId);

  void update(@Valid @NotNull OutsourcingInvoiceRequests.UpdateRequest request);

  void determine(@Valid @NotNull OutsourcingInvoiceRequests.DetermineRequest request);

  void receive(@Valid @NotNull OutsourcingInvoiceRequests.ReceiveRequest request);

  void invoice(@Valid @NotNull OutsourcingInvoiceRequests.InvoiceRequest request);

  OutsourcingInvoiceData generate(@Valid @NotNull OutsourcingInvoiceRequests.GenerateRequest request);

  List<OutsourcingInvoiceData> getAll(@Valid @NotNull OutsourcingOrderId orderId);

}
