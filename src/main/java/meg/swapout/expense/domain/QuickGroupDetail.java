package meg.swapout.expense.domain;


import javax.persistence.*;

@Entity
public class QuickGroupDetail {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	@OneToOne
	@JoinColumn(name = "catid")
	private Category category;

	private Double percentage;
	@ManyToOne
	@JoinColumn(name = "quickgroup")
	private QuickGroup quickgroup;

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
	public Double getPercentage() {
		return percentage;
	}
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	public QuickGroup getQuickgroup() {
		return quickgroup;
	}
	public void setQuickgroup(QuickGroup quickgroup) {
		this.quickgroup = quickgroup;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
