package pico.erp.outsourcing.invoice;

import java.util.Optional;
import kkojaeh.spring.boot.component.ComponentAutowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.company.CompanyData;
import pico.erp.company.CompanyId;
import pico.erp.company.CompanyService;
import pico.erp.invoice.InvoiceData;
import pico.erp.invoice.InvoiceId;
import pico.erp.invoice.InvoiceService;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.DetermineRequest;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests.ReceiveRequest;
import pico.erp.project.ProjectService;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserData;
import pico.erp.user.UserId;
import pico.erp.user.UserService;

@Mapper
public abstract class OutsourcingInvoiceMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @ComponentAutowired
  protected ItemService itemService;

  @ComponentAutowired
  protected ItemSpecService itemSpecService;

  @ComponentAutowired
  private CompanyService companyService;

  @ComponentAutowired
  private UserService userService;

  @Lazy
  @Autowired
  private OutsourcingInvoiceRepository outsourcingInvoiceRepository;

  @ComponentAutowired
  private ProjectService projectService;


  @ComponentAutowired
  private InvoiceService invoiceService;

  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract OutsourcingInvoiceEntity jpa(OutsourcingInvoice data);

  public OutsourcingInvoice jpa(OutsourcingInvoiceEntity entity) {
    return OutsourcingInvoice.builder()
      .id(entity.getId())
      .dueDate(entity.getDueDate())
      .orderId(entity.getOrderId())
      .invoiceId(entity.getInvoiceId())
      .remark(entity.getRemark())
      .status(entity.getStatus())
      .build();
  }

  protected UserData map(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::get)
      .orElse(null);
  }

  protected Auditor auditor(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::getAuditor)
      .orElse(null);
  }

  protected CompanyData map(CompanyId companyId) {
    return Optional.ofNullable(companyId)
      .map(companyService::get)
      .orElse(null);
  }

  public OutsourcingInvoice map(OutsourcingInvoiceId outsourcingInvoiceId) {
    return Optional.ofNullable(outsourcingInvoiceId)
      .map(id -> outsourcingInvoiceRepository.findBy(id)
        .orElseThrow(OutsourcingInvoiceExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }


  protected InvoiceData map(InvoiceId invoiceId) {
    return Optional.ofNullable(invoiceId)
      .map(invoiceService::get)
      .orElse(null);
  }

  public abstract OutsourcingInvoiceData map(OutsourcingInvoice outsourcingInvoice);

  public abstract OutsourcingInvoiceMessages.Create.Request map(
    OutsourcingInvoiceRequests.CreateRequest request);

  public abstract OutsourcingInvoiceMessages.Update.Request map(
    OutsourcingInvoiceRequests.UpdateRequest request);

  public abstract OutsourcingInvoiceMessages.Invoice.Request map(
    OutsourcingInvoiceRequests.InvoiceRequest request);

  public abstract OutsourcingInvoiceMessages.Determine.Request map(
    DetermineRequest request);

  public abstract OutsourcingInvoiceMessages.Receive.Request map(
    ReceiveRequest request);

  public abstract OutsourcingInvoiceMessages.Cancel.Request map(
    OutsourcingInvoiceRequests.CancelRequest request);

  public abstract void pass(
    OutsourcingInvoiceEntity from, @MappingTarget OutsourcingInvoiceEntity to);


}


