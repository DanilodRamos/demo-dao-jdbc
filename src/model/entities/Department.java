package model.entities;

import java.io.Serializable;

public class Department implements Serializable {

	private static final long serialVersionUID = 1L;
//atributos
	private Integer id;
	private String name;

	// construtor padrao
	public Department() {
	}

	// construtor com argumentos
	public Department(Integer id, String name) {

		this.id = id;
		this.name = name;
	}

	// getters e setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// hashcode
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	// iCONS
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		return Objects.equals(id, other.id);
	}

	// toSTRING PADRAO
	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + "]";
	}
	// implements serializable no começo
}
