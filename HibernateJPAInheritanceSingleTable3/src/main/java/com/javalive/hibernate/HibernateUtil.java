package com.javalive.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.javalive.entity.Account;
import com.javalive.entity.CreditAccount;
import com.javalive.entity.DebitAccount;

/**
 * The single table strategy maps all entities of the inheritance structure to
 * the same database table. This approach makes polymorphic queries very
 * efficient and provides the best performance. Note that when no explicit
 * inheritance strategy is registered, Hibernate/JPA will choose the
 * SINGLE_TABLE inheritance strategy by default. SINGLE_TABLE inheritance
 * performs the best in terms of executed SQL statements. However, you cannot
 * use NOT NULL constraints on the column-level. You can still use triggers and
 * rules to enforce such constraints, but it’s not as straightforward. Each
 * subclass in a hierarchy must define a unique discriminator value, which is
 * used to differentiate between rows belonging to separate subclass types. If
 * this is not specified, the DTYPE column is used as a discriminator, storing
 * the associated subclass name. You can optionally specify a discriminator
 * column name. This column is registered by the @DiscriminatorColumn if omitted
 * the default DTYPE name is used.
 * Syntax: @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
 */
public class HibernateUtil {

	private static StandardServiceRegistry registry;
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

				Map<String, Object> settings = new HashMap<String, Object>();
				settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
				settings.put(Environment.URL, "jdbc:mysql://localhost:3306/javalive?useSSL=false");
				settings.put(Environment.USER, "root");
				settings.put(Environment.PASS, "root");
				settings.put(Environment.HBM2DDL_AUTO, "create");
				settings.put(Environment.SHOW_SQL, "true");
				registryBuilder.applySettings(settings);
				registry = registryBuilder.build();

				MetadataSources sources = new MetadataSources(registry).addAnnotatedClass(Account.class)
						.addAnnotatedClass(CreditAccount.class).addAnnotatedClass(DebitAccount.class);

				Metadata metadata = sources.getMetadataBuilder().build();

				// To apply logging Interceptor using session factory
				sessionFactory = metadata.getSessionFactoryBuilder()
						// .applyInterceptor(new LoggingInterceptor())
						.build();
			} catch (Exception e) {
				if (registry != null) {
					StandardServiceRegistryBuilder.destroy(registry);
				}
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}

	public static void shutdown() {
		if (registry != null) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
}
