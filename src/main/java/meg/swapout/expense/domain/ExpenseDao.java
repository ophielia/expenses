package meg.swapout.expense.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Table;


@Entity
@Table(name = "expense")
@SqlResultSetMapping(name="ExpenseResult", 
entities={ 
    @EntityResult(entityClass= meg.swapout.expense.domain.ExpenseDao.class, fields={
        @FieldResult(name="id", column="id"),
        @FieldResult(name="transid", column="transid"),
        @FieldResult(name="transdate", column="transdate"),
        @FieldResult(name="hascat", column="hascat"),
        @FieldResult(name="description", column="description"),
        @FieldResult(name="detail", column="detail"),
        @FieldResult(name="transtotal", column="transtotal"),
        @FieldResult(name="cattransid", column="cattransid"),
        @FieldResult(name="catamount", column="catamount"),
        @FieldResult(name="catid", column="catid"),
        @FieldResult(name="dispCat", column="dispcat"),
        @FieldResult(name="catName", column="catname"),
        @FieldResult(name="nonexpense", column="nonexpense"),
        @FieldResult(name="deleted", column="deleted"),
        @FieldResult(name="comment", column="comment"),
        @FieldResult(name="month", column="month"),
        @FieldResult(name="year", column="year"),
        @FieldResult(name="source", column="source"),
        @FieldResult(name="displayamount", column="displayamount"),
        @FieldResult(name="monthyear", column="monthyear"),
        })},
columns={}
)
public class ExpenseDao {

	@Id
	private String id;
	private Long transid;
	private Date transdate;
	private Boolean hascat;
	private String description;
	private String detail;
	private Double transtotal;
	private Long cattransid;
	private Double catamount;
	private Long catid;
	@Column(name="dispcat")
	private String dispCat;
	@Column(name="catname")
	private String catName;
	private Boolean nonexpense;
	private Boolean deleted;
	private String comment;
	private String month;
	private Long year;
	private Integer source;
	private Double displayamount;
	private String monthyear;


	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getCatamount() {
        return this.catamount;
    }

	public Long getCatid() {
        return this.catid;
    }

	public String getCatName() {
        return this.catName;
    }

	public Long getCattransid() {
        return this.cattransid;
    }

	public String getComment() {
        return this.comment;
    }

	public Boolean getDeleted() {
        return this.deleted;
    }

	public String getDescription() {
        return this.description;
    }

	public String getDetail() {
        return this.detail;
    }

	public String getDispCat() {
        return this.dispCat;
    }

	public Boolean getHascat() {
        return this.hascat;
    }

	public String getMonth() {
        return this.month;
    }

	public Boolean getNonexpense() {
        return this.nonexpense;
    }

	public Integer getSource() {
        return this.source;
    }

	public Date getTransdate() {
        return this.transdate;
    }

	public Long getTransid() {
        return this.transid;
    }

	public Double getTranstotal() {
        return this.transtotal;
    }

	public Long getYear() {
        return this.year;
    }

	public void setCatamount(Double catamount) {
        this.catamount = catamount;
    }

	public void setCatid(Long catid) {
        this.catid = catid;
    }

	public void setCatName(String catName) {
        this.catName = catName;
    }

	public void setCattransid(Long cattransid) {
        this.cattransid = cattransid;
    }

	public void setComment(String comment) {
        this.comment = comment;
    }

	public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

	public void setDescription(String description) {
        this.description = description;
    }

	public void setDetail(String detail) {
        this.detail = detail;
    }

	public void setDispCat(String dispCat) {
        this.dispCat = dispCat;
    }

	public void setHascat(Boolean hascat) {
        this.hascat = hascat;
    }

	public void setMonth(String month) {
        this.month = month;
    }

	public void setNonexpense(Boolean nonexpense) {
        this.nonexpense = nonexpense;
    }

	public void setSource(Integer source) {
        this.source = source;
    }

	public void setTransdate(Date transdate) {
        this.transdate = transdate;
    }

	public void setTransid(Long transid) {
        this.transid = transid;
    }

	public void setTranstotal(Double transtotal) {
        this.transtotal = transtotal;
    }

	public void setYear(Long year) {
        this.year = year;
    }

	public Double getDisplayamount() {
		return displayamount;
	}

	public void setDisplayamount(Double displayamount) {
		this.displayamount = displayamount;
	}

	public String getMonthyear() {
		return monthyear;
	}

	public void setMonthyear(String monthyear) {
		this.monthyear = monthyear;
	}

	

}
