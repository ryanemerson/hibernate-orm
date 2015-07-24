package org.hibernate.test;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * @author Ryan Emerson
 */
public class NotNullNotEnforced extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {Parent.class, Child.class};
	}

	@Test(expected = ConstraintViolationException.class)
	public void testNotNullPersistCollection() {
		Parent parent = new Parent();

		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.persist( parent );
		t.commit();
		s.close();
	}

	@Test(expected = ConstraintViolationException.class)
	public void testNotNullMergeCollection() {
		Parent parent = new Parent();

		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.merge( parent );
		t.commit();
		s.close();
	}

	@Entity
	@Table(name = "PARENT")
	static class Parent {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
		@NotNull
		private List<Child> children;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public List<Child> getChildren() {
			return children;
		}

		public void setChildren(List<Child> children) {
			this.children = children;
		}
	}

	@Entity
	@Table(name = "CHILD")
	static class Child {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne
		private Parent parent;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Parent getParent() {
			return parent;
		}

		public void setParent(Parent parent) {
			this.parent = parent;
		}
	}
}