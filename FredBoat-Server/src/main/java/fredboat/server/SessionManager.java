package fredboat.server;

import fredboat.common.Crypto;
import fredboat.common.db.DatabaseManager;
import fredboat.common.db.entities.UserSession;
import java.util.List;
import javax.persistence.EntityManager;

public class SessionManager {
    
    public static void mergeSession(UserSession us){
        EntityManager em = DatabaseManager.getEntityManager();
        
        em.getTransaction().begin();
        em.merge(us);
        em.getTransaction().commit();
    }
    
    public static UserSession findSession(long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        
        return em.find(UserSession.class, id);
    }
    
    public static UserSession findSession(String token){
        EntityManager em = DatabaseManager.getEntityManager();
        List list = em.createQuery("SELECT ses FROM user_session ses WHERE ses.token = :token").setParameter("token", Crypto.hash(token)).getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return (UserSession) list.get(0);
    }
    
}
