package pico.erp.outsourcing.invoice.item;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.event.Event;

public interface OutsourcingInvoiceItemEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-invoice-item.created";

    private OutsourcingInvoiceItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-invoice-item.updated";

    private OutsourcingInvoiceItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeletedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-invoice-item.deleted";

    private OutsourcingInvoiceItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class InvoicedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-invoice-item.invoiced";

    private OutsourcingInvoiceItemId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class GeneratedEvent implements Event {

    public final static String CHANNEL = "event.outsourcing-invoice-item.generated";

    private List<OutsourcingInvoiceItemId> ids;

    public String channel() {
      return CHANNEL;
    }

  }
}
