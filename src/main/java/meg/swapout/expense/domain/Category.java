package meg.swapout.expense.domain;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



@Entity
@Table(name = "CATEGORY")
public class Category implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	@Version
    @Column(name = "version")
    private Integer version;
	
	@NotNull
	@Size(max = 100)
	private String name;

	@Size(max = 300)
	private String description;
	
	private Boolean nonexpense;
	
	private Boolean displayinlist;

	@Transient
	private Category parentCategory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getNonexpense() {
		return nonexpense;
	}

	public void setNonexpense(Boolean nonexpense) {
		this.nonexpense = nonexpense;
	}

	public Boolean getDisplayInList() {
		return displayinlist;
	}

	public void setDisplayInList(Boolean displayinlist) {
		this.displayinlist = displayinlist;
	}

	
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

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
}
