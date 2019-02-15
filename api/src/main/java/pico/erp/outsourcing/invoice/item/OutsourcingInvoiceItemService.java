package pico.erp.outsourcing.invoice.item;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;

public interface OutsourcingInvoiceItemService {

  OutsourcingInvoiceItemData create(
    @Valid @NotNull OutsourcingInvoiceItemRequests.CreateRequest request);

  void delete(@Valid @NotNull OutsourcingInvoiceItemRequests.DeleteRequest request);

  boolean exists(@Valid @NotNull OutsourcingInvoiceItemId id);

  OutsourcingInvoiceItemData get(@Valid @NotNull OutsourcingInvoiceItemId id);

  List<OutsourcingInvoiceItemData> getAll(OutsourcingInvoiceId invoiceId);

  void update(@Valid @NotNull OutsourcingInvoiceItemRequests.UpdateRequest request);

  void generate(@Valid @NotNull OutsourcingInvoiceItemRequests.GenerateRequest request);

  void invoice(@Valid @NotNull OutsourcingInvoiceItemRequests.InvoiceRequest request);


}
