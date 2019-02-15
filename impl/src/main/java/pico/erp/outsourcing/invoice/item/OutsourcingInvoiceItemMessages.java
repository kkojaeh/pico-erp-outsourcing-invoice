package pico.erp.outsourcing.invoice.item;

import java.math.BigDecimal;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.outsourcing.invoice.OutsourcingInvoice;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.event.Event;

public interface OutsourcingInvoiceItemMessages {

  interface Create {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class Request {

      @Valid
      @NotNull
      OutsourcingInvoiceItemId id;

      @NotNull
      OutsourcingInvoice invoice;

      @NotNull
      OutsourcingOrderItemId orderItemId;


      @NotNull
      @Min(0)
      BigDecimal quantity;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Update {

    @Data
    class Request {

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Delete {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Invoice {

    @Data
    class Request {

      InvoiceItemId invoiceItemId;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

}
