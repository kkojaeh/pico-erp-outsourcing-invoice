package pico.erp.outsourcing.invoice.item;

import java.time.format.DateTimeFormatter;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.invoice.item.InvoiceItemRequests;
import pico.erp.invoice.item.InvoiceItemService;
import pico.erp.item.lot.ItemLotService;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceEvents;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceService;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemRequests;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;

@SuppressWarnings("unused")
@Component
public class OutsourcingInvoiceItemEventListener {

  private static final String LISTENER_NAME = "listener.outsourcing-invoice-item-event-listener";

  @Autowired
  private OutsourcingInvoiceItemService outsourcingInvoiceItemService;

  @ComponentAutowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @ComponentAutowired
  private InvoiceItemService invoiceItemService;

  @Autowired
  private OutsourcingInvoiceService outsourcingInvoiceService;

  @ComponentAutowired
  private ItemLotService itemLotService;

  @ComponentAutowired
  private ItemSpecService itemSpecService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceEvents.GeneratedEvent.CHANNEL)
  public void onOutsourcingInvoiceGenerated(OutsourcingInvoiceEvents.GeneratedEvent event) {

    outsourcingInvoiceItemService.generate(
      new OutsourcingInvoiceItemRequests.GenerateRequest(event.getId())
    );
  }

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceEvents.InvoicedEvent.CHANNEL)
  public void onOutsourcingInvoiceInvoiced(OutsourcingInvoiceEvents.InvoicedEvent event) {
    val outsourcingInvoice = outsourcingInvoiceService.get(event.getId());

    outsourcingInvoiceItemService.getAll(event.getId())
      .forEach(item -> {
        val orderItem = outsourcingOrderItemService.get(item.getOrderItemId());
        val itemId = orderItem.getItemId();
        val itemSpecCode = orderItem.getItemSpecCode();
        val invoiceItemId = InvoiceItemId.generate();
        invoiceItemService.create(
          InvoiceItemRequests.CreateRequest.builder()
            .id(invoiceItemId)
            .invoiceId(outsourcingInvoice.getInvoiceId())
            .itemId(itemId)
            .itemSpecCode(itemSpecCode)
            .quantity(item.getQuantity())
            .unit(orderItem.getUnit())
            .remark(item.getRemark())
            .build()
        );
        outsourcingInvoiceItemService.invoice(
          OutsourcingInvoiceItemRequests.InvoiceRequest.builder()
            .id(item.getId())
            .invoiceItemId(invoiceItemId)
            .build()
        );
      });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceEvents.ReceivedEvent.CHANNEL)
  public void onOutsourcingInvoiceReceived(OutsourcingInvoiceEvents.ReceivedEvent event) {

    outsourcingInvoiceItemService.getAll(event.getId())
      .forEach(invoiceItem -> {
        outsourcingOrderItemService.receive(
          OutsourcingOrderItemRequests.ReceiveRequest.builder()
            .id(invoiceItem.getOrderItemId())
            .quantity(invoiceItem.getQuantity())
            .build()
        );
      });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingInvoiceItemEvents.UpdatedEvent.CHANNEL)
  public void onOutsourcingInvoiceItemUpdated(OutsourcingInvoiceItemEvents.UpdatedEvent event) {
    val outsourcingInvoiceItem = outsourcingInvoiceItemService.get(event.getId());
    val invoiceItemId = outsourcingInvoiceItem.getInvoiceItemId();
    if(invoiceItemId != null) {
      invoiceItemService.update(
        InvoiceItemRequests.UpdateRequest.builder()
          .id(invoiceItemId)
          .quantity(outsourcingInvoiceItem.getQuantity())
          .remark(outsourcingInvoiceItem.getRemark())
          .build()
      );
    }
  }


}
