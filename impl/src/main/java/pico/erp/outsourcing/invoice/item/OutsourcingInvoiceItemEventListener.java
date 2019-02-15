package pico.erp.outsourcing.invoice.item;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.invoice.item.InvoiceItemRequests;
import pico.erp.invoice.item.InvoiceItemService;
import pico.erp.item.lot.ItemLotCode;
import pico.erp.item.lot.ItemLotId;
import pico.erp.item.lot.ItemLotKey;
import pico.erp.item.lot.ItemLotRequests;
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

  @Lazy
  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @Lazy
  @Autowired
  private InvoiceItemService invoiceItemService;

  @Lazy
  @Autowired
  private OutsourcingInvoiceService outsourcingInvoiceService;

  @Lazy
  @Autowired
  private ItemLotService itemLotService;

  @Lazy
  @Autowired
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
        ItemLotId lotId = null;
        val itemSpecCode = orderItem.getItemSpecCode();
        val itemLotCode = ItemLotCode.from(dateFormatter.format(OffsetDateTime.now()));
        val lotKey = ItemLotKey.from(itemId, itemSpecCode, itemLotCode);
        val exists = itemLotService.exists(lotKey);
        if (exists) {
          lotId = itemLotService.get(lotKey).getId();
        } else {
          lotId = ItemLotId.generate();
          itemLotService.create(
            ItemLotRequests.CreateRequest.builder()
              .id(lotId)
              .itemId(itemId)
              .specCode(itemSpecCode)
              .lotCode(itemLotCode)
              .build()
          );
        }
        val invoiceItemId = InvoiceItemId.generate();
        invoiceItemService.create(
          InvoiceItemRequests.CreateRequest.builder()
            .id(invoiceItemId)
            .invoiceId(outsourcingInvoice.getInvoiceId())
            .itemId(orderItem.getItemId())
            .itemLotId(lotId)
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
