package pico.erp.outsourcing.invoice.item

import kkojaeh.spring.boot.component.SpringBootTestComponent
import kkojaeh.spring.boot.component.Take
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.bom.BomApplication
import pico.erp.company.CompanyApplication
import pico.erp.delivery.DeliveryApplication
import pico.erp.document.DocumentApplication
import pico.erp.invoice.InvoiceApplication
import pico.erp.item.ItemApplication
import pico.erp.outsourcing.invoice.*
import pico.erp.outsourcing.order.OutsourcingOrderApplication
import pico.erp.outsourcing.order.OutsourcingOrderId
import pico.erp.outsourcing.order.OutsourcingOrderRequests
import pico.erp.outsourcing.order.OutsourcingOrderService
import pico.erp.outsourcing.order.item.OutsourcingOrderItemId
import pico.erp.outsourcing.request.OutsourcingRequestApplication
import pico.erp.process.ProcessApplication
import pico.erp.project.ProjectApplication
import pico.erp.shared.TestParentApplication
import pico.erp.user.UserApplication
import pico.erp.warehouse.WarehouseApplication
import spock.lang.Specification

@SpringBootTest(classes = [OutsourcingInvoiceApplication, TestConfig])
@SpringBootTestComponent(parent = TestParentApplication, siblings = [
  UserApplication, ItemApplication, ProjectApplication, ProcessApplication, CompanyApplication,
  OutsourcingOrderApplication, InvoiceApplication, DocumentApplication, DeliveryApplication,
  OutsourcingRequestApplication, WarehouseApplication, BomApplication
])
@Transactional
@Rollback
@ActiveProfiles("test")
class OutsourcingInvoiceItemServiceSpec extends Specification {

  @Autowired
  OutsourcingInvoiceService invoiceService

  @Autowired
  OutsourcingInvoiceItemService invoiceItemService

  @Take
  OutsourcingOrderService orderService

  def invoiceId = OutsourcingInvoiceId.from("outsourcing-invoice-test")

  def id = OutsourcingInvoiceItemId.from("outsourcing-invoice-item-1")

  def orderItemId = OutsourcingOrderItemId.from("outsourcing-order-a-1")

  def unknownId = OutsourcingInvoiceItemId.from("unknown")

  def orderId = OutsourcingOrderId.from("outsourcing-order-a")

  def setup() {
    orderService.determine(
      new OutsourcingOrderRequests.DetermineRequest(
        id: orderId
      )
    )
    orderService.send(
      new OutsourcingOrderRequests.SendRequest(
        id: orderId
      )
    )
  }

  def cancelInvoice() {
    invoiceService.cancel(
      new OutsourcingInvoiceRequests.CancelRequest(
        id: invoiceId
      )
    )
  }

  def determineInvoice() {
    invoiceService.determine(
      new OutsourcingInvoiceRequests.DetermineRequest(
        id: invoiceId
      )
    )
  }

  def receiveInvoice() {
    invoiceService.receive(
      new OutsourcingInvoiceRequests.ReceiveRequest(
        id: invoiceId
      )
    )
  }

  def createItem() {
    invoiceItemService.create(
      new OutsourcingInvoiceItemRequests.CreateRequest(
        id: id,
        invoiceId: invoiceId,
        orderItemId: orderItemId,
        quantity: 100,
        remark: "품목 비고"
      )
    )
  }

  def createItem2() {
    invoiceItemService.create(
      new OutsourcingInvoiceItemRequests.CreateRequest(
        id: OutsourcingInvoiceItemId.from("outsourcing-invoice-item-2"),
        invoiceId: invoiceId,
        orderItemId: orderItemId,
        quantity: 100,
        remark: "품목 비고"
      )
    )
  }

  def updateItem() {
    invoiceItemService.update(
      new OutsourcingInvoiceItemRequests.UpdateRequest(
        id: id,
        quantity: 200,
        remark: "품목 비고2",
      )
    )
  }

  def deleteItem() {
    invoiceItemService.delete(
      new OutsourcingInvoiceItemRequests.DeleteRequest(
        id: id
      )
    )
  }


  def "존재 - 아이디로 확인"() {
    when:
    createItem()
    def exists = invoiceItemService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = invoiceItemService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    createItem()
    def item = invoiceItemService.get(id)
    then:
    item.id == id
    item.orderItemId == orderItemId
    item.invoiceId == invoiceId
    item.quantity == 100
    item.remark == "품목 비고"

  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    invoiceItemService.get(unknownId)

    then:
    thrown(OutsourcingInvoiceItemExceptions.NotFoundException)
  }

  def "생성 - 작성 후 생성"() {
    when:
    createItem()
    def items = invoiceItemService.getAll(invoiceId)
    then:
    items.size() > 0
  }

  def "생성 - 확정 후 생성"() {
    when:
    createItem()
    determineInvoice()
    createItem2()
    def items = invoiceItemService.getAll(invoiceId)
    then:
    items.size() == 2

  }


  def "생성 - 취소 후 생성"() {
    when:
    createItem()
    cancelInvoice()
    createItem2()
    then:
    thrown(OutsourcingInvoiceItemExceptions.CannotCreateException)
  }


  def "생성 - 수령 후 생성"() {
    when:
    createItem()
    determineInvoice()
    receiveInvoice()
    createItem2()
    then:
    thrown(OutsourcingInvoiceItemExceptions.CannotCreateException)
  }

  def "수정 - 작성 후 수정"() {
    when:
    createItem()
    updateItem()
    def item = invoiceItemService.get(id)

    then:
    item.quantity == 200
    item.remark == "품목 비고2"
  }

  def "수정 - 확정 후 수정"() {
    when:
    createItem()
    determineInvoice()
    updateItem()
    def item = invoiceItemService.get(id)

    then:
    item.quantity == 200
    item.remark == "품목 비고2"

  }

  def "수정 - 취소 후 수정"() {
    when:
    createItem()
    cancelInvoice()
    updateItem()
    then:
    thrown(OutsourcingInvoiceItemExceptions.CannotUpdateException)
  }


  def "수정 - 수령 후 수정"() {
    when:
    createItem()
    determineInvoice()
    receiveInvoice()
    updateItem()
    then:
    thrown(OutsourcingInvoiceItemExceptions.CannotUpdateException)
  }


  def "삭제 - 작성 후 삭제"() {
    when:
    createItem()
    deleteItem()
    def items = invoiceItemService.getAll(invoiceId)
    then:
    items.size() == 0
  }

  def "삭제 - 확정 후 삭제"() {
    when:
    createItem()
    determineInvoice()
    deleteItem()
    def items = invoiceItemService.getAll(invoiceId)
    then:
    items.size() == 0

  }

  def "삭제 - 취소 후 삭제"() {
    when:
    createItem()
    cancelInvoice()
    deleteItem()
    then:
    thrown(OutsourcingInvoiceItemExceptions.CannotDeleteException)
  }

  def "삭제 - 수령 후 삭제"() {
    when:
    createItem()
    determineInvoice()
    receiveInvoice()
    deleteItem()
    then:
    thrown(OutsourcingInvoiceItemExceptions.CannotDeleteException)
  }


}
