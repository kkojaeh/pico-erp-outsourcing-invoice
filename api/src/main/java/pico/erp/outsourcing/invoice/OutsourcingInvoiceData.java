package pico.erp.outsourcing.invoice;

import java.time.OffsetDateTime;
import lombok.Data;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.order.OutsourcingOrderId;

@Data
public class OutsourcingInvoiceData {

  OutsourcingInvoiceId id;

  OutsourcingOrderId orderId;

  InvoiceId invoiceId;

  OffsetDateTime dueDate;

  OutsourcingInvoiceStatusKind status;

  String remark;

  boolean cancelable;

  boolean receivable;

  boolean determinable;

  boolean updatable;

}
