package pico.erp.outsourcing.invoice.item;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceService;
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemRequests.DeleteRequest;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class OutsourcingInvoiceItemServiceLogic implements OutsourcingInvoiceItemService {

  @Autowired
  private OutsourcingInvoiceItemRepository itemRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private OutsourcingInvoiceItemMapper mapper;

  @Lazy
  @Autowired
  private OutsourcingInvoiceService invoiceService;

  @Lazy
  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;


  @Override
  public OutsourcingInvoiceItemData create(OutsourcingInvoiceItemRequests.CreateRequest request) {
    val item = new OutsourcingInvoiceItem();
    val response = item.apply(mapper.map(request));
    if (itemRepository.exists(item.getId())) {
      throw new OutsourcingInvoiceItemExceptions.AlreadyExistsException();
    }
    val created = itemRepository.create(item);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(DeleteRequest request) {
    val item = itemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    itemRepository.deleteBy(item.getId());
    eventPublisher.publishEvents(response.getEvents());
  }



  @Override
  public boolean exists(OutsourcingInvoiceItemId id) {
    return itemRepository.exists(id);
  }


  @Override
  public OutsourcingInvoiceItemData get(OutsourcingInvoiceItemId id) {
    return itemRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(OutsourcingInvoiceItemExceptions.NotFoundException::new);
  }

  @Override
  public List<OutsourcingInvoiceItemData> getAll(OutsourcingInvoiceId invoiceId) {
    return itemRepository.findAllBy(invoiceId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public void update(OutsourcingInvoiceItemRequests.UpdateRequest request) {
    val item = itemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    itemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void generate(OutsourcingInvoiceItemRequests.GenerateRequest request) {
    val invoice = invoiceService.get(request.getInvoiceId());
    val orderItems = outsourcingOrderItemService.getAll(invoice.getOrderId());
    val createRequests = orderItems.stream().map(item -> OutsourcingInvoiceItemRequests.CreateRequest.builder()
      .id(OutsourcingInvoiceItemId.generate())
      .invoiceId(invoice.getId())
      .orderItemId(item.getId())
      .quantity(BigDecimal.ZERO)
      .remark(item.getRemark())
      .build()
    ).collect(Collectors.toList());
    createRequests.forEach(this::create);
    eventPublisher.publishEvent(
      new OutsourcingInvoiceItemEvents.GeneratedEvent(
        createRequests.stream()
          .map(OutsourcingInvoiceItemRequests.CreateRequest::getId)
          .collect(Collectors.toList())
      )
    );
  }

  @Override
  public void invoice(OutsourcingInvoiceItemRequests.InvoiceRequest request) {
    val item = itemRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    itemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

}
