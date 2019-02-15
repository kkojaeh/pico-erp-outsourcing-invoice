package pico.erp.outsourcing.invoice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.invoice.InvoiceRequests
import pico.erp.invoice.InvoiceService
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemService
import pico.erp.outsourcing.order.OutsourcingOrderId
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.UserId
import spock.lang.Specification

import java.time.OffsetDateTime

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class OutsourcingInvoiceServiceSpec extends Specification {

  @Autowired
  OutsourcingInvoiceService outsourcingInvoiceService

  @Autowired
  OutsourcingInvoiceItemService invoiceItemService

  @Lazy
  @Autowired
  InvoiceService invoiceService

  def id = OutsourcingInvoiceId.from("outsourcing-invoice-1")

  def id2 = OutsourcingInvoiceId.from("outsourcing-invoice-2")

  def unknownId = OutsourcingInvoiceId.from("unknown")

  def dueDate = OffsetDateTime.now().plusDays(7)

  def remark = "요청 비고"

  def dueDate2 = OffsetDateTime.now().plusDays(8)

  def orderId = OutsourcingOrderId.from("outsourcing-order-b")

  def remark2 = "요청 비고2"

  def confirmerId = UserId.from("kjh")


  def setup() {
    outsourcingInvoiceService.create(
      new OutsourcingInvoiceRequests.CreateRequest(
        id: id,
        orderId:  orderId,
        dueDate: dueDate,
        remark: remark
      )
    )
  }

  def createInvoice2() {
    outsourcingInvoiceService.create(
      new OutsourcingInvoiceRequests.CreateRequest(
        id: id2,
        orderId:  orderId,
        dueDate: dueDate,
        remark: remark
      )
    )
  }

  def cancelInvoice() {
    outsourcingInvoiceService.cancel(
      new OutsourcingInvoiceRequests.CancelRequest(
        id: id
      )
    )
  }

  def determineInvoice() {
    outsourcingInvoiceService.determine(
      new OutsourcingInvoiceRequests.DetermineRequest(
        id: id
      )
    )
  }



  def receiveInvoice() {
    outsourcingInvoiceService.receive(
      new OutsourcingInvoiceRequests.ReceiveRequest(
        id: id
      )
    )
  }


  def updateInvoice() {
    outsourcingInvoiceService.update(
      new OutsourcingInvoiceRequests.UpdateRequest(
        id: id,
        dueDate: dueDate2,
        remark: remark2
      )
    )
  }


  def receiveInvoiceBy() {
    def invoice = outsourcingInvoiceService.get(id)
    invoiceService.receive(
      new InvoiceRequests.ReceiveRequest(
        id: invoice.invoiceId,
        confirmerId: confirmerId
      )
    )
  }

  def "자동생성 - 발주를 통해 자동 생성" () {
    when:
    determineInvoice()
    def id = OutsourcingInvoiceId.from("outsourcing-invoice-generated")
    def generated = outsourcingInvoiceService.generate(
      new OutsourcingInvoiceRequests.GenerateRequest(
        id: id,
        orderId: orderId
      )
    )
    def invoice = outsourcingInvoiceService.get(generated.id)
    def items = invoiceItemService.getAll(generated.id)
    then:
    generated.id == id
    invoice.orderId == orderId
    items.size() == 2

  }

  def "생성 - 작성중인 송장 존재" () {
    when:
    createInvoice2()
    then:
    thrown(OutsourcingInvoiceExceptions.DraftAlreadyExistsException)

  }

  def "생성 - 확정 한 송장 존재" () {
    when:
    determineInvoice()
    createInvoice2()
    def invoice = outsourcingInvoiceService.get(id2)
    then:
    invoice.orderId == orderId

  }


  def "존재 - 아이디로 존재 확인"() {
    when:
    def exists = outsourcingInvoiceService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = outsourcingInvoiceService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def invoice = outsourcingInvoiceService.get(id)

    then:
    invoice.id == id
    invoice.remark == remark
    invoice.dueDate == dueDate
    invoice.orderId == orderId

  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    outsourcingInvoiceService.get(unknownId)

    then:
    thrown(OutsourcingInvoiceExceptions.NotFoundException)
  }


  def "수정 - 취소 후 수정"() {
    when:
    cancelInvoice()
    updateInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotUpdateException)
  }


  def "수정 - 확정 후 수정"() {
    when:
    determineInvoice()
    updateInvoice()
    def invoice = outsourcingInvoiceService.get(id)
    then:
    invoice.dueDate == dueDate2
    invoice.remark == remark2
  }

  def "수정 - 수령 후 수정"() {
    when:
    determineInvoice()
    receiveInvoice()
    updateInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotUpdateException)
  }


  def "수정 - 작성 후 수정"() {
    when:
    updateInvoice()
    def invoice = outsourcingInvoiceService.get(id)

    then:
    invoice.dueDate == dueDate2
    invoice.remark == remark2
  }

  def "확정 - 작성 후 확정"() {
    when:
    determineInvoice()
    def invoice = outsourcingInvoiceService.get(id)
    then:
    invoice.status == OutsourcingInvoiceStatusKind.DETERMINED
    invoice.invoiceId != null
  }

  def "확정 - 확정 후 확정"() {
    when:
    determineInvoice()
    determineInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotDetermineException)
  }


  def "확정 - 취소 후 확정"() {
    when:
    cancelInvoice()
    determineInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotDetermineException)
  }

  def "확정 - 수령 후 확정"() {
    when:
    determineInvoice()
    receiveInvoice()
    determineInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotDetermineException)
  }

  def "취소 - 취소 후에는 취소"() {
    when:
    cancelInvoice()
    cancelInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotCancelException)
  }

  def "취소 - 확정 후 취소"() {
    when:
    determineInvoice()
    cancelInvoice()
    def invoice = outsourcingInvoiceService.get(id)
    then:
    invoice.status == OutsourcingInvoiceStatusKind.CANCELED
  }


  def "취소 - 수령 후 취소"() {
    when:
    determineInvoice()
    receiveInvoice()
    cancelInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotCancelException)
  }

  def "수령 - 작성 후 수령"() {
    when:
    receiveInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotReceiveException)
  }

  def "수령 - 확정 후 수령"() {
    when:
    determineInvoice()
    receiveInvoiceBy()
    def invoice = outsourcingInvoiceService.get(id)
    then:
    invoice.status == OutsourcingInvoiceStatusKind.RECEIVED

  }


  def "수령 - 취소 후 수령"() {
    when:
    cancelInvoice()
    receiveInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotReceiveException)
  }


  def "수령 - 수령 후 수령"() {
    when:
    determineInvoice()
    receiveInvoice()
    receiveInvoice()
    then:
    thrown(OutsourcingInvoiceExceptions.CannotReceiveException)
  }


}
