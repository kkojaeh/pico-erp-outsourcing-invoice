package pico.erp.outsourcing.invoice.item;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemId;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutsourcingInvoiceItemData {

  OutsourcingInvoiceItemId id;

  OutsourcingInvoiceId invoiceId;

  OutsourcingOrderItemId orderItemId;

  InvoiceItemId invoiceItemId;

  BigDecimal quantity;

  String remark;

}
