package pico.erp.outsourcing.invoice.item;

import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemId;
import pico.erp.shared.TypeDefinitions;

public interface OutsourcingInvoiceItemRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    OutsourcingInvoiceItemId id;

    @Valid
    @NotNull
    OutsourcingInvoiceId invoiceId;

    @Valid
    @NotNull
    OutsourcingOrderItemId orderItemId;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    OutsourcingInvoiceItemId id;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DeleteRequest {

    @Valid
    @NotNull
    OutsourcingInvoiceItemId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class GenerateRequest {

    @Valid
    @NotNull
    OutsourcingInvoiceId invoiceId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class InvoiceRequest {

    @Valid
    @NotNull
    OutsourcingInvoiceItemId id;

    @Valid
    @NotNull
    InvoiceItemId invoiceItemId;

  }
}
