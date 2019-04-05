package pico.erp.outsourcing.invoice;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceEvents.DeterminedEvent;
import pico.erp.outsourcing.order.OutsourcingOrderId;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutsourcingInvoice implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  OutsourcingInvoiceId id;

  OutsourcingOrderId orderId;

  InvoiceId invoiceId;

  OffsetDateTime dueDate;

  String remark;

  OutsourcingInvoiceStatusKind status;

  public OutsourcingInvoice() {

  }

  public OutsourcingInvoiceMessages.Create.Response apply(
    OutsourcingInvoiceMessages.Create.Request request) {
    this.id = request.getId();
    this.orderId = request.getOrderId();
    this.dueDate = request.getDueDate();
    this.remark = request.getRemark();
    this.status = OutsourcingInvoiceStatusKind.DRAFT;
    return new OutsourcingInvoiceMessages.Create.Response(
      Arrays.asList(new OutsourcingInvoiceEvents.CreatedEvent(this.id))
    );
  }

  public OutsourcingInvoiceMessages.Update.Response apply(
    OutsourcingInvoiceMessages.Update.Request request) {
    if (!isUpdatable()) {
      throw new OutsourcingInvoiceExceptions.CannotUpdateException();
    }
    this.dueDate = request.getDueDate();
    this.remark = request.getRemark();
    return new OutsourcingInvoiceMessages.Update.Response(
      Arrays.asList(new OutsourcingInvoiceEvents.UpdatedEvent(this.id))
    );
  }

  public OutsourcingInvoiceMessages.Determine.Response apply(
    OutsourcingInvoiceMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new OutsourcingInvoiceExceptions.CannotDetermineException();
    }
    this.status = OutsourcingInvoiceStatusKind.DETERMINED;
    return new OutsourcingInvoiceMessages.Determine.Response(
      Arrays.asList(new DeterminedEvent(this.id))
    );
  }

  public OutsourcingInvoiceMessages.Cancel.Response apply(
    OutsourcingInvoiceMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new OutsourcingInvoiceExceptions.CannotCancelException();
    }
    this.status = OutsourcingInvoiceStatusKind.CANCELED;
    return new OutsourcingInvoiceMessages.Cancel.Response(
      Arrays.asList(new OutsourcingInvoiceEvents.CanceledEvent(this.id))
    );
  }

  public OutsourcingInvoiceMessages.Receive.Response apply(
    OutsourcingInvoiceMessages.Receive.Request request) {
    if (!isReceivable()) {
      throw new OutsourcingInvoiceExceptions.CannotReceiveException();
    }
    this.status = OutsourcingInvoiceStatusKind.RECEIVED;
    return new OutsourcingInvoiceMessages.Receive.Response(
      Arrays.asList(new OutsourcingInvoiceEvents.ReceivedEvent(this.id))
    );
  }

  public OutsourcingInvoiceMessages.Invoice.Response apply(
    OutsourcingInvoiceMessages.Invoice.Request request) {
    if (!isInvoiceable()) {
      throw new OutsourcingInvoiceExceptions.CannotInvoiceException();
    }
    this.invoiceId = request.getInvoiceId();
    return new OutsourcingInvoiceMessages.Invoice.Response(
      Arrays.asList(new OutsourcingInvoiceEvents.InvoicedEvent(this.id))
    );
  }


  public boolean isCancelable() {
    return status.isCancelable();
  }

  public boolean isReceivable() {
    return status.isReceivable();
  }

  public boolean isDeterminable() {
    return status.isDeterminable();
  }

  public boolean isUpdatable() {
    return status.isUpdatable();
  }

  public boolean isInvoiceable() {
    return status.isInvoiceable() && invoiceId == null;
  }


}
