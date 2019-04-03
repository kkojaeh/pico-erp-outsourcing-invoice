package pico.erp.outsourcing.invoice.item;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.invoice.item.InvoiceItemData;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.invoice.item.InvoiceItemService;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.lot.ItemLotData;
import pico.erp.item.lot.ItemLotId;
import pico.erp.item.lot.ItemLotService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.invoice.OutsourcingInvoice;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceExceptions;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceMapper;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.shared.data.Auditor;

@Mapper
public abstract class OutsourcingInvoiceItemMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemLotService itemLotService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Lazy
  @Autowired
  private OutsourcingInvoiceItemRepository outsourcingRequestItemRepository;

  @Autowired
  private OutsourcingInvoiceMapper requestMapper;

  @Lazy
  @Autowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @Lazy
  @Autowired
  private InvoiceItemService invoiceItemService;

  protected OutsourcingInvoiceItemId id(OutsourcingInvoiceItem outsourcingRequestItem) {
    return outsourcingRequestItem != null ? outsourcingRequestItem.getId() : null;
  }

  @Mappings({
    @Mapping(target = "invoiceId", source = "invoice.id"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract OutsourcingInvoiceItemEntity jpa(OutsourcingInvoiceItem data);

  public OutsourcingInvoiceItem jpa(OutsourcingInvoiceItemEntity entity) {
    return OutsourcingInvoiceItem.builder()
      .id(entity.getId())
      .invoice(map(entity.getInvoiceId()))
      .orderItemId(entity.getOrderItemId())
      .invoiceItemId(entity.getInvoiceItemId())
      .quantity(entity.getQuantity())
      .remark(entity.getRemark())
      .build();
  }

  public OutsourcingInvoiceItem map(OutsourcingInvoiceItemId outsourcingRequestItemId) {
    return Optional.ofNullable(outsourcingRequestItemId)
      .map(id -> outsourcingRequestItemRepository.findBy(id)
        .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemLotData map(ItemLotId itemLotId) {
    return Optional.ofNullable(itemLotId)
      .map(itemLotService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }

  protected OutsourcingInvoice map(OutsourcingInvoiceId outsourcingInvoiceId) {
    return requestMapper.map(outsourcingInvoiceId);
  }


  protected InvoiceItemData map(InvoiceItemId invoiceItemId) {
    return Optional.ofNullable(invoiceItemId)
      .map(invoiceItemService::get)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "invoiceId", source = "invoice.id")
  })
  public abstract OutsourcingInvoiceItemData map(OutsourcingInvoiceItem item);

  @Mappings({
    @Mapping(target = "invoice", source = "invoiceId")
  })
  public abstract OutsourcingInvoiceItemMessages.Create.Request map(
    OutsourcingInvoiceItemRequests.CreateRequest request);

  public abstract OutsourcingInvoiceItemMessages.Invoice.Request map(
    OutsourcingInvoiceItemRequests.InvoiceRequest request);

  public abstract OutsourcingInvoiceItemMessages.Update.Request map(
    OutsourcingInvoiceItemRequests.UpdateRequest request);

  public abstract OutsourcingInvoiceItemMessages.Delete.Request map(
    OutsourcingInvoiceItemRequests.DeleteRequest request);


  public abstract void pass(
    OutsourcingInvoiceItemEntity from, @MappingTarget OutsourcingInvoiceItemEntity to);


}



