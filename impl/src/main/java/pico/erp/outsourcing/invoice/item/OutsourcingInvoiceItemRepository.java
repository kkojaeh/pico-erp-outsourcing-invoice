package pico.erp.outsourcing.invoice.item;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;

@Repository
public interface OutsourcingInvoiceItemRepository {

  OutsourcingInvoiceItem create(@NotNull OutsourcingInvoiceItem item);

  void deleteBy(@NotNull OutsourcingInvoiceItemId id);

  boolean exists(@NotNull OutsourcingInvoiceItemId id);

  Stream<OutsourcingInvoiceItem> findAllBy(@NotNull OutsourcingInvoiceId planId);

  Optional<OutsourcingInvoiceItem> findBy(@NotNull OutsourcingInvoiceItemId id);

  void update(@NotNull OutsourcingInvoiceItem item);

}
