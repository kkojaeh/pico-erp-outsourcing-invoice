package pico.erp.outsourcing.invoice.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.outsourcing.invoice.OutsourcingInvoice;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemId;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutsourcingInvoiceItem implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  OutsourcingInvoiceItemId id;

  OutsourcingInvoice invoice;

  OutsourcingOrderItemId orderItemId;

  InvoiceItemId invoiceItemId;

  BigDecimal quantity;

  String remark;


  public OutsourcingInvoiceItem() {

  }

  public OutsourcingInvoiceItemMessages.Create.Response apply(
    OutsourcingInvoiceItemMessages.Create.Request request) {
    if (!request.getInvoice().isUpdatable()) {
      throw new OutsourcingInvoiceItemExceptions.CannotCreateException();
    }
    this.id = request.getId();
    this.invoice = request.getInvoice();
    this.orderItemId = request.getOrderItemId();
    this.quantity = request.getQuantity();
    this.remark = request.getRemark();

    return new OutsourcingInvoiceItemMessages.Create.Response(
      Arrays.asList(new OutsourcingInvoiceItemEvents.CreatedEvent(this.id))
    );
  }

  public OutsourcingInvoiceItemMessages.Update.Response apply(
    OutsourcingInvoiceItemMessages.Update.Request request) {
    if (!this.invoice.isUpdatable()) {
      throw new OutsourcingInvoiceItemExceptions.CannotUpdateException();
    }
    this.quantity = request.getQuantity();
    this.remark = request.getRemark();
    return new OutsourcingInvoiceItemMessages.Update.Response(
      Arrays.asList(new OutsourcingInvoiceItemEvents.UpdatedEvent(this.id))
    );
  }

  public OutsourcingInvoiceItemMessages.Delete.Response apply(
    OutsourcingInvoiceItemMessages.Delete.Request request) {
    if (!this.invoice.isUpdatable()) {
      throw new OutsourcingInvoiceItemExceptions.CannotDeleteException();
    }
    return new OutsourcingInvoiceItemMessages.Delete.Response(
      Arrays.asList(new OutsourcingInvoiceItemEvents.DeletedEvent(this.id))
    );
  }

  public OutsourcingInvoiceItemMessages.Invoice.Response apply(
    OutsourcingInvoiceItemMessages.Invoice.Request request) {
    if(invoiceItemId != null){
      throw new OutsourcingInvoiceItemExceptions.CannotInvoiceException();
    }
    this.invoiceItemId = request.getInvoiceItemId();
    return new OutsourcingInvoiceItemMessages.Invoice.Response(
      Arrays.asList(new OutsourcingInvoiceItemEvents.InvoicedEvent(this.id))
    );
  }


}
