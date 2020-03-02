package dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.stereotype.Service;

import bean.Client;
import bean.Commentaire;
import bean.Elementpanier;
import bean.Film;
import bean.Panier;
import data.Data;

@Service
public class Dao {

	static EntityManagerFactory emf = null;
	static EntityManager em = null;

	public Dao() {
		try {
			emf = Persistence.createEntityManagerFactory(Data.persistenceUnit);
			em = emf.createEntityManager();
			System.out.println("init Dao OK");
		} catch (Exception e) {
			System.out.println("Erreur init Dao " + e.getMessage());
		}
	}

	public void close() {
		em.clear();
		emf.close();
	}

	public List<Client> listerClients() {
		List<Client> lst = em.createQuery("select u from Client u").getResultList();
		return lst;

	}

	public boolean enregistrerClient(Client u) {

		em.getTransaction().begin();
		em.persist(u);
		em.getTransaction().commit();

		return true;
	}

	//FILM
	public List<Film> listerFilms() {
		List<Film> lst = Dao.em.createQuery("select u from Film u").getResultList();
		return lst;
	}
	
	//FILM
	public Film getFilm(int id) {
		Film film = (Film) Dao.em.createQuery("select u from Film u where u.idFilm LIKE :id").setParameter("id", id).getSingleResult();
		return film;
	}
	
	public Client RechercheUtilisateur(String login, String password) {
		List<Client> c =  em.createQuery("select u from Client u where u.login = :login AND u.password = :password")
				.setParameter("login", login)
				.setParameter("password", password)
				.getResultList();
		if(c.size() != 0) {
			return c.get(0);
		}
		return null;
	}
	
	
	//PANIER
	public Panier enregistrerPanier(String adresseFacturation, Integer client_idClient) {
		Panier pan = new Panier(adresseFacturation, client_idClient);
		//Commentaire
		em.getTransaction().begin();
		em.persist(pan);
		em.getTransaction().commit();
		return pan;
	}	

	public Panier getPanier(int id) {
		Panier panier = (Panier) em.createQuery("select u from Panier u where u.idPanier LIKE :id").setParameter("id", id).getSingleResult();
		System.out.println(panier.getAdresseFacturation());
		return panier;
	}
	

	public Panier GetIdPanier(int idClient) {
		Panier panier = (Panier) em.createQuery("select u from Panier u where u.Client_idClient LIKE :id").setParameter("id", idClient).getSingleResult();
		return panier;
	}
	
	//COMMENTAIRE	
	public List<Commentaire> listerCommentaire(int id) {
		List<Commentaire> lst = em.createQuery("select u from Commentaire u where u.Film_idFilm LIKE :id").setParameter("id", id).getResultList();
		System.out.println("*****" + lst.get(0).getTexte());
		return lst;
	}
	//pas testé
	public Commentaire getCommentaire(int id) {
		Commentaire commentaire = (Commentaire) em.createQuery("select u from Commentaire u where u.idCommentaire LIKE :id").setParameter("id", id).getSingleResult();
		return commentaire;
	}

	public List<Commentaire> enregistrerCommentaire(int client_idClient, int film_idFilm, String texte, Date date) {
		Commentaire com = new Commentaire(client_idClient, film_idFilm, texte, date);
		//Commentaire
		em.getTransaction().begin();
		em.persist(com);
		em.getTransaction().commit();
		return this.listerCommentaire(com.getFilm_idFilm());
	}	
	
	//TODO : UpdateCommentaire
	//TODO : Supprimer Commentaire
	
	
	
	//CONTROLLEUR ELEMENT PANIER
	
	public List<Film> getElementPanier(int id) {
		//recuperer l'id du panier d'un utilisateur
		Panier unPanier = (Panier)  em.createQuery("select u from Panier u where u.Client_idClient LIKE :id").setParameter("id", id).getSingleResult();
		if(unPanier == null ) {
			return null;
		}
		
		//recuperer la liste des id des films qui compose le panier
		List<Elementpanier> elemnts = em.createQuery("select u from Elementpanier u where u.panier_idPanier LIKE :id").setParameter("id", unPanier.getIdPanier()).getResultList();
		List<Film> films = new ArrayList<Film>(); 
		
		if(elemnts == null || elemnts.isEmpty() ) {
			return null;
		}
		
		//recuperer les informations des films
		for (int  i = 0 ; i < elemnts.size()  ; i++){
			films.add(getFilm(elemnts.get(i).getFilm_idFilm()));
		}
        	return films;
	}
	
	public List<Elementpanier> estDejaDansPanier(int idPanier, int idFilm) {
		List<Elementpanier> elemnts = em.createQuery("select u from Elementpanier u where u.panier_idPanier = :login AND u.Film_idFilm = :password")
				.setParameter("login", idPanier)
				.setParameter("password", idFilm)
				.getResultList();

		
		return elemnts;
	}
	
	
}
