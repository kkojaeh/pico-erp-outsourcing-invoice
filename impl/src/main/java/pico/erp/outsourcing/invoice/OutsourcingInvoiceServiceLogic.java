package pico.erp.outsourcing.invoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kkojaeh.spring.boot.component.Give;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.CancelRequest;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.DetermineRequest;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.GenerateRequest;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.InvoiceRequest;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.ReceiveRequest;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.OutsourcingOrderService;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Give
@Transactional
@Validated
public class OutsourcingInvoiceServiceLogic implements OutsourcingInvoiceService {

  @Autowired
  private OutsourcingInvoiceRepository outsourcingInvoiceRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private OutsourcingInvoiceMapper mapper;

  @Lazy
  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Override
  public void cancel(CancelRequest request) {
    val outsourcingInvoice = outsourcingInvoiceRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
    val response = outsourcingInvoice.apply(mapper.map(request));
    outsourcingInvoiceRepository.update(outsourcingInvoice);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public OutsourcingInvoiceData create(OutsourcingInvoiceRequests.CreateRequest request) {
    val hasDraft = outsourcingInvoiceRepository.findAllBy(request.getOrderId())
      .anyMatch(invoice -> invoice.getStatus() == OutsourcingInvoiceStatusKind.DRAFT);
    if (hasDraft) {
      throw new OutsourcingInvoiceExceptions.DraftAlreadyExistsException();
    }
    val outsourcingInvoice = new OutsourcingInvoice();
    val response = outsourcingInvoice.apply(mapper.map(request));
    if (outsourcingInvoiceRepository.exists(outsourcingInvoice.getId())) {
      throw new OutsourcingInvoiceExceptions.AlreadyExistsException();
    }
    val created = outsourcingInvoiceRepository.create(outsourcingInvoice);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public boolean exists(OutsourcingInvoiceId id) {
    return outsourcingInvoiceRepository.exists(id);
  }

  @Override
  public boolean exists(InvoiceId invoiceId) {
    return outsourcingInvoiceRepository.exists(invoiceId);
  }

  @Override
  public OutsourcingInvoiceData get(OutsourcingInvoiceId id) {
    return outsourcingInvoiceRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
  }

  @Override
  public OutsourcingInvoiceData get(InvoiceId invoiceId) {
    return outsourcingInvoiceRepository.findBy(invoiceId)
      .map(mapper::map)
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
  }

  @Override
  public void update(OutsourcingInvoiceRequests.UpdateRequest request) {
    val outsourcingInvoice = outsourcingInvoiceRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
    val response = outsourcingInvoice.apply(mapper.map(request));
    outsourcingInvoiceRepository.update(outsourcingInvoice);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void determine(DetermineRequest request) {
    val outsourcingInvoice = outsourcingInvoiceRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
    val response = outsourcingInvoice.apply(mapper.map(request));
    outsourcingInvoiceRepository.update(outsourcingInvoice);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void receive(ReceiveRequest request) {
    val outsourcingInvoice = outsourcingInvoiceRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
    val response = outsourcingInvoice.apply(mapper.map(request));
    outsourcingInvoiceRepository.update(outsourcingInvoice);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void invoice(InvoiceRequest request) {
    val outsourcingInvoice = outsourcingInvoiceRepository.findBy(request.getId())
      .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new);
    val response = outsourcingInvoice.apply(mapper.map(request));
    outsourcingInvoiceRepository.update(outsourcingInvoice);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public OutsourcingInvoiceData generate(GenerateRequest request) {
    val order = outsourcingOrderService.get(request.getOrderId());
    val id = request.getId();
    val createRequest = OutsourcingInvoiceRequests.CreateRequest.builder()
      .id(id)
      .orderId(order.getId())
      .dueDate(LocalDateTime.now().plusDays(1))
      .build();
    val created = create(createRequest);
    eventPublisher.publishEvent(
      new OutsourcingInvoiceEvents.GeneratedEvent(created.getId())
    );
    return created;
  }

  @Override
  public List<OutsourcingInvoiceData> getAll(OutsourcingOrderId orderId) {
    return outsourcingInvoiceRepository.findAllBy(orderId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

}
