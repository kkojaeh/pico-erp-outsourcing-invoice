package pico.erp.outsourcing.invoice;

import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.invoice.InvoiceEvents;
import pico.erp.invoice.InvoiceId;
import pico.erp.invoice.InvoiceRequests;
import pico.erp.invoice.InvoiceService;
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemService;
import pico.erp.outsourcing.order.OutsourcingOrderService;

@SuppressWarnings("unused")
@Component
@Transactional
public class OutsourcingInvoiceEventListener {

  private static final String LISTENER_NAME = "listener.outsourcing-invoice-event-listener";

  @ComponentAutowired
  private InvoiceService invoiceService;

  @Lazy
  @Autowired
  private OutsourcingInvoiceService outsourcingInvoiceService;

  @Lazy
  @Autowired
  private OutsourcingInvoiceItemService outsourcingInvoiceItemService;

  @ComponentAutowired
  private OutsourcingOrderService outsourcingOrderService;


  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceEvents.DeterminedEvent.CHANNEL)
  public void onOutsourcingInvoiceDetermined(OutsourcingInvoiceEvents.DeterminedEvent event) {
    val outsourcingInvoice = outsourcingInvoiceService.get(event.getId());
    val outsourcingOrder = outsourcingOrderService.get(outsourcingInvoice.getOrderId());
    val invoiceId = InvoiceId.generate();
    invoiceService.create(
      InvoiceRequests.CreateRequest.builder()
        .id(invoiceId)
        .dueDate(outsourcingInvoice.getDueDate())
        .receiverId(outsourcingOrder.getReceiverId())
        .senderId(outsourcingOrder.getSupplierId())
        .receiveAddress(outsourcingOrder.getReceiveAddress())
        .remark(outsourcingInvoice.getRemark())
        .build()
    );
    outsourcingInvoiceService.invoice(
      OutsourcingInvoiceRequests.InvoiceRequest.builder()
        .id(outsourcingInvoice.getId())
        .invoiceId(invoiceId)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceEvents.UpdatedEvent.CHANNEL)
  public void onOutsourcingInvoiceUpdated(OutsourcingInvoiceEvents.UpdatedEvent event) {
    val outsourcingInvoice = outsourcingInvoiceService.get(event.getId());
    val outsourcingOrder = outsourcingOrderService.get(outsourcingInvoice.getOrderId());
    val invoiceId = outsourcingInvoice.getInvoiceId();
    if (invoiceId != null) {
      invoiceService.update(
        InvoiceRequests.UpdateRequest.builder()
          .id(invoiceId)
          .dueDate(outsourcingInvoice.getDueDate())
          .receiverId(outsourcingOrder.getReceiverId())
          .senderId(outsourcingOrder.getSupplierId())
          .receiveAddress(outsourcingOrder.getReceiveAddress())
          .remark(outsourcingInvoice.getRemark())
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceEvents.CanceledEvent.CHANNEL)
  public void onOutsourcingInvoiceCanceled(OutsourcingInvoiceEvents.CanceledEvent event) {
    val outsourcingInvoice = outsourcingInvoiceService.get(event.getId());
    val outsourcingOrder = outsourcingOrderService.get(outsourcingInvoice.getOrderId());
    val invoiceId = outsourcingInvoice.getInvoiceId();
    if (invoiceId != null) {
      invoiceService.cancel(
        InvoiceRequests.CancelRequest.builder()
          .id(invoiceId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + InvoiceEvents.ReceivedEvent.CHANNEL)
  public void onInvoiceReceived(InvoiceEvents.ReceivedEvent event) {
    val invoiceId = event.getId();
    val exists = outsourcingInvoiceService.exists(invoiceId);
    if (exists) {
      val outsourcingInvoice = outsourcingInvoiceService.get(invoiceId);
      outsourcingInvoiceService.receive(
        OutsourcingInvoiceRequests.ReceiveRequest.builder()
          .id(outsourcingInvoice.getId())
          .build()
      );
    }
  }


}
