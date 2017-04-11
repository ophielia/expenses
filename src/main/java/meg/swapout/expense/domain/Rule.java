package meg.swapout.expense.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CATEGORYRULE")
public class Rule {
	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	private Long lineorder;

	@NotNull
	private String containing;
	
/*	@NotNull
	@Column(name = "category_id")
	private Long categoryId;
	*/

	@OneToOne
	@JoinColumn(name="category_id")
	private Category category;

	@Transient
	private String catDisplay;


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


	public Long getLineorder() {
		return lineorder;
	}


	public void setLineorder(Long lineorder) {
		this.lineorder = lineorder;
	}


	public String getContaining() {
		return containing;
	}


	public void setContaining(String containing) {
		this.containing = containing;
	}

/*
	public Long getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
*/

	public String getCatDisplay() {
		return catDisplay;
	}


	public void setCatDisplay(String catDisplay) {
		this.catDisplay = catDisplay;
	}


	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
