package org.hibernate.test.onetomany;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolationException;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * @author Ryan Emerson
 */
public class MergeNotNullCollectionTest extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {Parent.class, Child.class};
	}

	@Test
	public void testOneToManyNotNullCollection() {
		Parent parent = new Parent();
		Child child = new Child();

		List<Child> children = new ArrayList<Child>();
		children.add( child );

		child.setParent( parent );
		parent.setChildren( children );

		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.merge( parent );
		t.commit();
		s.close();
	}

//	@Test(expected = ConstraintViolationException.class)
//	public void testOneToManyNullCollection() {
//		Parent parent = new Parent();
//		Child child = new Child();
//		child.setParent( parent );
//
//		Session s = openSession();
//		Transaction t = s.beginTransaction();
//		s.merge( parent );
//		t.commit();
//		s.close();
//	}

	@Entity
	@Table(name = "PARENT")
	static class Parent {

		@Id
//		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
		@NotNull
		@ValidateOffspring
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
//		@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public static class ValidateOffspringValidator implements ConstraintValidator<ValidateOffspring, List<?>> {

		@Override
		public void initialize(ValidateOffspring constraintAnnotation) {
		}

		@Override
		public boolean isValid(List<?> items, ConstraintValidatorContext context) {
			System.out.println("items! := " + items);
			if (items == null || items.isEmpty()) {
				return false;
			}

			return true;
		}
	}

	@Documented
	@Constraint(validatedBy = ValidateOffspringValidator.class)
	@java.lang.annotation.Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ValidateOffspring {

		String message() default "{be.smals.cascadingdemo.validator.ValidateOffspring}";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}
}
