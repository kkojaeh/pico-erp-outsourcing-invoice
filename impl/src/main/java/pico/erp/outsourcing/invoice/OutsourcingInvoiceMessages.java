package pico.erp.outsourcing.invoice;

import java.time.OffsetDateTime;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Value;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.event.Event;

public interface OutsourcingInvoiceMessages {

  interface Create {

    @Data
    class Request {

      @Valid
      @NotNull
      OutsourcingInvoiceId id;

      @NotNull
      OutsourcingOrderId orderId;

      @Future
      @NotNull
      OffsetDateTime dueDate;

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

      @Future
      @NotNull
      OffsetDateTime dueDate;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Determine {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Receive {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Cancel {

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

      InvoiceId invoiceId;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }




}
