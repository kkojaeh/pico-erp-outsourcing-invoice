package pico.erp.outsourcing.invoice.item;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;

@Repository
interface OutsourcingInvoiceItemEntityRepository extends
  CrudRepository<OutsourcingInvoiceItemEntity, OutsourcingInvoiceItemId> {

  @Query("SELECT i FROM OutsourcingInvoiceItem i WHERE i.invoiceId = :invoiceId ORDER BY i.createdDate")
  Stream<OutsourcingInvoiceItemEntity> findAllBy(@Param("invoiceId") OutsourcingInvoiceId invoiceId);

}

@Repository
@Transactional
public class OutsourcingInvoiceItemRepositoryJpa implements OutsourcingInvoiceItemRepository {

  @Autowired
  private OutsourcingInvoiceItemEntityRepository repository;

  @Autowired
  private OutsourcingInvoiceItemMapper mapper;

  @Override
  public OutsourcingInvoiceItem create(OutsourcingInvoiceItem planItem) {
    val entity = mapper.jpa(planItem);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(OutsourcingInvoiceItemId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(OutsourcingInvoiceItemId id) {
    return repository.exists(id);
  }

  @Override
  public Stream<OutsourcingInvoiceItem> findAllBy(OutsourcingInvoiceId invoiceId) {
    return repository.findAllBy(invoiceId)
      .map(mapper::jpa);
  }

  @Override
  public Optional<OutsourcingInvoiceItem> findBy(OutsourcingInvoiceItemId id) {
    return Optional.ofNullable(repository.findOne(id))
      .map(mapper::jpa);
  }

  @Override
  public void update(OutsourcingInvoiceItem planItem) {
    val entity = repository.findOne(planItem.getId());
    mapper.pass(mapper.jpa(planItem), entity);
    repository.save(entity);
  }
}
