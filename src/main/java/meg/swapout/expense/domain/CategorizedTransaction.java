package meg.swapout.expense.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "CATTRANS")
public class CategorizedTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    @NotNull
    @OneToOne
    @JoinColumn(name = "catid")
    private Category category;

    @NotNull
    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date createdon;


    @Size(max = 300)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "banktaid")
    private RawTransaction banktrans;

    @Transient
    private Double displayAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Deprecated
    public Long getCatid() {
        return this.category!=null?category.getId():0L;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public RawTransaction getBanktrans() {
        return banktrans;
    }

    public void setBanktrans(RawTransaction banktrans) {
        this.banktrans = banktrans;
    }

    public Double getDisplayAmount() {
        if (this.amount==null) {
            return 0D;
        }
        return this.amount*-1;
    }

    public void setDisplayAmount(Double displayAmount) {
        if (displayAmount!=null) {
            this.amount = displayAmount*-1;
        }
    }


}
