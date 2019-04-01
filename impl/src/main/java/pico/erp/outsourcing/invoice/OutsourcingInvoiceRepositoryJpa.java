package pico.erp.outsourcing.invoice;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.invoice.InvoiceId;
import pico.erp.outsourcing.order.OutsourcingOrderId;

@Repository
interface OutsourcingInvoiceEntityRepository extends
  CrudRepository<OutsourcingInvoiceEntity, OutsourcingInvoiceId> {

  @Query("SELECT i FROM OutsourcingInvoice i WHERE i.orderId = :orderId ORDER BY i.createdDate")
  Stream<OutsourcingInvoiceEntity> findAllBy(@Param("orderId") OutsourcingOrderId orderId);

  @Query("SELECT i FROM OutsourcingInvoice i WHERE i.invoiceId = :invoiceId")
  Optional<OutsourcingInvoiceEntity> findBy(@Param("invoiceId") InvoiceId invoiceId);

  @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM OutsourcingInvoice i WHERE i.invoiceId = :invoiceId")
  boolean exists(@Param("invoiceId") InvoiceId invoiceId);

}

@Repository
@Transactional
public class OutsourcingInvoiceRepositoryJpa implements OutsourcingInvoiceRepository {

  @Autowired
  private OutsourcingInvoiceEntityRepository repository;

  @Autowired
  private OutsourcingInvoiceMapper mapper;

  @Override
  public OutsourcingInvoice create(OutsourcingInvoice plan) {
    val entity = mapper.jpa(plan);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(OutsourcingInvoiceId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(OutsourcingInvoiceId id) {
    return repository.existsById(id);
  }

  @Override
  public boolean exists(InvoiceId invoiceId) {
    return repository.exists(invoiceId);
  }

  @Override
  public Optional<OutsourcingInvoice> findBy(OutsourcingInvoiceId id) {
    return repository.findById(id)
      .map(mapper::jpa);
  }

  @Override
  public Optional<OutsourcingInvoice> findBy(InvoiceId invoiceId) {
    return repository.findBy(invoiceId)
      .map(mapper::jpa);
  }

  @Override
  public Stream<OutsourcingInvoice> findAllBy(OutsourcingOrderId orderId) {
    return repository.findAllBy(orderId)
      .map(mapper::jpa);
  }

  @Override
  public void update(OutsourcingInvoice plan) {
    val entity = repository.findById(plan.getId()).get();
    mapper.pass(mapper.jpa(plan), entity);
    repository.save(entity);
  }
}
