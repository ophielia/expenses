package meg.swapout.expense.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "TARGETDETAIL")
public class TargetDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

	/*@NotNull
    @Min(1)
	private Long catid;*/

    @OneToOne
    @JoinColumn(name = "catid")
    private Category category;

    @NotNull
    @Min(1)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "targetgroup")
    private Target targetgroup;

    @Transient
    private String catdisplay;

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

    public Long getCatid() {
        return category.getId();
    }

    public void setCatid(Long catid) {
        //nuthin - delete this!!
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Target getTargetgroup() {
        return targetgroup;
    }

    public void setTargetgroup(Target targetgroup) {
        this.targetgroup = targetgroup;
    }

    public String getCatdisplay() {
        return catdisplay;
    }

    public void setCatdisplay(String catdisplay) {
        this.catdisplay = catdisplay;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
