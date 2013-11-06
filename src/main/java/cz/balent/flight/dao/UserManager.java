/**
 * Copyright 2013 Robert Balent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.balent.flight.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

import cz.balent.flight.model.User;
/**
 * Handles User entity CRUD operation above database layer.
 * 
 * @author Robert Balent
 *
 */
public class UserManager implements Serializable {

	private static final long serialVersionUID = -8700519476090078378L;

	@PersistenceContext
	EntityManager entityManager;

	@Inject
	UserTransaction utx;

	public User create(User user) {
		try {
			utx.begin();
			entityManager.persist(user);
			entityManager.flush();
			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		return user;
	}

	public List<User> getAllUsers() {
		List<User> users = null;
		try {
			utx.begin();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> userRoot = cq.from(User.class);
			cq.select(userRoot);
			TypedQuery<User> q = entityManager.createQuery(cq);
			users = q.getResultList();

			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return users;
	}

	public User getUser(final String name) {
		User user = null;
		try {
			utx.begin();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> userRoot = cq.from(User.class);
			cq.where(cb.equal(userRoot.get("id"), name));
			TypedQuery<User> q = entityManager.createQuery(cq);
			user = q.getSingleResult();

			utx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return user;
	}
}
