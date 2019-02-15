create table osi_outsourcing_invoice (
	id binary(16) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	due_date datetime,
	invoice_id binary(16),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	outsourcing_order_id binary(16),
	remark varchar(50),
	status varchar(20),
	primary key (id)
) engine=InnoDB;

create table osi_outsourcing_invoice_item (
	id binary(16) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	invoice_id binary(16),
	invoice_item_id binary(16),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	order_item_id binary(16),
	quantity decimal(19,2),
	remark varchar(50),
	primary key (id)
) engine=InnoDB;

alter table osi_outsourcing_invoice
	add constraint UK3nlcyyd4o3nqq73xilbqts3y4 unique (invoice_id);

create index IDXsb1p4n5x2pqqhqk9tw4w1cmrp
	on osi_outsourcing_invoice_item (invoice_id);
