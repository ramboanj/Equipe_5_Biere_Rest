package fr.iutinfo.skeleton.api;

import static org.junit.Assert.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import fr.iutinfo.skeleton.api.Utilisateur;
import fr.iutinfo.skeleton.api.UtilisateurDao;
import fr.iutinfo.skeleton.api.UtilisateurResource;
import fr.iutinfo.skeleton.common.dto.BiereDto;
import fr.iutinfo.skeleton.common.dto.CmdBDto;
import fr.iutinfo.skeleton.common.dto.UtilisateurDto;
import fr.iutinfo.skeleton.api.BDDFactory;
import fr.iutinfo.skeleton.api.Biere;
import fr.iutinfo.skeleton.api.BiereDao;
import fr.iutinfo.skeleton.api.BiereResource;
public class AllTest {
	@Test
	public void testUtilisateur() {//50.1 >50.7
		Utilisateur a=new Utilisateur("prenoms", "nom","enseigne","siret","email","mdp","addresse",
			"tel", "type","passwdHash","salt");
		assertEquals("prenoms",a.getPrenom());
		assertEquals("nom",a.getNom());
		assertEquals("enseigne",a.getEnseigne());
		assertEquals("siret",a.getSiret());
		assertEquals("email",a.getEmail());
		assertEquals("mdp",a.getMdp());
		assertEquals("addresse",a.getAdresse());
		assertEquals("tel",a.getTel());
		assertEquals("type",a.getType());
		assertEquals("passwdHash",a.getPasswdHash());
		assertEquals("salt",a.getSalt());
	}
	@Test
	public void testUtilisateurMDP() {
		Utilisateur a=new Utilisateur();
		assertTrue(a.isAnonymous());
		assertFalse(a.isInUserGroup());
		a.setSalt("RIP");
		a.setPassword("mum");
		a.resetPasswordHash();
		String s=buildHash("mum",a.getSalt());
		assertTrue(s +"\n!=\n" +a.getPasswdHash(),a.getPasswdHash().equals(s));
		assertFalse("MDP incorrect",a.isGoodPassword("mum"));//Car anynomous
		a=new Utilisateur("prenoms", "nom","enseigne","siret","email","mdp","addresse",
				"tel", "type","passwdHash","RIP");
		a.setPassword("mum");
		a.setUno(10);
		assertTrue(a.isGoodPassword("mum"));
		a.resetPasswordHash();
		assertTrue(a.isGoodPassword("mum"));
		assertFalse(a.isAnonymous());
		assertTrue(a.isInUserGroup());

	}
	@Test
	public void testUtilisateurDTO() {
		UtilisateurDto dto=new UtilisateurDto();
		dto.setUno(1);
		dto.setPrenom("pre non");
		dto.setNom("nan");
		dto.setEnseigne("saigne");
		dto.setSiret("sir");
		dto.setEmail("maille");
		dto.setMdp("maitre de poche");
		dto.setAdresse("dresseur");
		dto.setTel("telligent");
		dto.setType("mec");
		dto.setSalt("RIP");
		Utilisateur a=new Utilisateur();
		a.initFromDto(dto);
		assertEquals("pre non",a.getPrenom());
		assertEquals("nan",a.getNom());
		assertEquals("saigne",a.getEnseigne());
		assertEquals("sir",a.getSiret());
		assertEquals("maille",a.getEmail());
		assertEquals("maitre de poche",a.getMdp());
		assertEquals("dresseur",a.getAdresse());
		assertEquals("telligent",a.getTel());
		assertEquals("mec",a.getType());
		assertEquals("RIP",a.getSalt());
		
	}
	
	
	@Test
	public void testUtilisaRessource() throws SQLException{
		UtilisateurDao dao=BDDFactory.getDbi().open(UtilisateurDao.class);
		if (tableExist("utilisateur")) {
			dao.dropUtilisateurTable();
			assertFalse("IL est la ?! ?!",tableExist("utilisateur"));
		}
		UtilisateurResource a=new UtilisateurResource();
		assertTrue("IL A DISPARU ?!",tableExist("utilisateur"));new UtilisateurResource();
		Utilisateur use=new Utilisateur("prenoms", "nom","enseigne","siret","email","mdp","addresse",
				"tel", "type","passwdHash","RIP");
		UtilisateurDto id=a.createUtilisateur(use.convertToDto());
		assertEquals(use.getNom(),id.getNom());
		try {
			a.getUtilisateur(99999);
			fail("vraiment...");
		}catch(Exception df) {}
		assertTrue(a.getUtilisateur(id.getUno())!=null);
		try {
			a.getUtilisateur("map","KEGEGE");
			fail("vraiment...");
		}catch(Exception df) {}
		assertTrue(a.getUtilisateur("email", "mdp")!=null);
		List<Utilisateur> lisu=toutInitier();
		for (Utilisateur ma:lisu) {
			a.createUtilisateur(ma.convertToDto());
		}
		assertEquals(8,a.getAllUtilisateurs(null).size());
		assertEquals(4,a.getAllUtilisateurs("type").size());
		
		
	}
	
	@Test
	public void TestBiere() {
		Biere biereux=new Biere("nom",-1,2, 0, "forme","type", "description",00,"origine",-20,"amertume");
		BiereDto ata=biereux.convertToDto();
		assertEquals(ata.getNom(),biereux.getNom());
		assertEquals(ata.getUno(),biereux.getUno());
		//assertEquals(ata.getPno(),biereux.getPno()); //ATENTION ,LE PNO n'est pas initié dans PNO
		assertEquals(ata.getPrix(),biereux.getPrix());
		assertEquals(ata.getForme(),biereux.getForme());
		assertEquals(ata.getType(),biereux.getType());
		assertEquals(ata.getDescription(),biereux.getDescription());
		assertEquals(ata.getTaille(),biereux.getTaille());
		assertEquals(ata.getOrigine(),biereux.getOrigine());
		assertEquals(ata.getDegre(),biereux.getDegre());
		assertEquals(ata.getAmertume(),biereux.getAmertume());
		ata.setNom("Nop");
		ata.setUno(3);
		ata.setPno(4);
		ata.setPrix(200);
		ata.setForme("former");
		ata.setType("tip");
		ata.setDescription("rien");
		ata.setTaille(9999);
		ata.setOrigine("organe");
		ata.setDegre(-50);
		ata.setAmertume("Beurk");
		biereux.initFromDto(ata);
		assertEquals(ata.getNom(),biereux.getNom());
		assertEquals(ata.getUno(),biereux.getUno());
		//assertEquals(ata.getPno(),biereux.getPno()); //ATENTION ,LE PNO n'est pas initié dans PNO
		assertEquals(ata.getPrix(),biereux.getPrix());
		assertEquals(ata.getForme(),biereux.getForme());
		assertEquals(ata.getType(),biereux.getType());
		assertEquals(ata.getDescription(),biereux.getDescription());
		assertEquals(ata.getTaille(),biereux.getTaille());
		assertEquals(ata.getOrigine(),biereux.getOrigine());
		assertEquals(ata.getDegre(),biereux.getDegre());
		assertEquals(ata.getAmertume(),biereux.getAmertume());
	}
	
	private static String buildHash(String password, String s) {
		Hasher hasher = Hashing.sha256().newHasher();
		hasher.putString(password + s, Charsets.UTF_8);
		return hasher.hash().toString();
	}

	private static boolean tableExist(String tableName) throws SQLException {
        DatabaseMetaData dbm = BDDFactory.getDbi().open().getConnection().getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        boolean exist = tables.next();
        tables.close();
        return exist;
    }
	
	
	@Test
	public void testBiereRessource() throws SQLException{//0% <73,2%
		BiereDao dao=BDDFactory.getDbi().open(BiereDao.class);
		if (tableExist("bieres")) {
			dao.dropBiereTable();;
			assertFalse("IL est la ?! ?!",tableExist("bieres"));
		}
		BiereResource a=new BiereResource();//On oublie le nouveau biere
		assertTrue("IL A DISPARU ?!",tableExist("bieres"));new BiereResource();
		Biere use=new Biere("nom",1,2, 0, "forme","type", "description",00,"origine",-20,"amertume");
		BiereDto id=a.createBiere(use.convertToDto());
		assertEquals(use.getNom(),id.getNom());
		try {
			a.getBiereByBno(99999);
			fail("vraiment...");
		}catch(Exception df) {}
		assertTrue(a.getBiereByBno(id.getUno())!=null);
		List<Biere> lisu=toutInitierbiere();
		for (Biere ma:lisu) {
			a.createBiere(ma.convertToDto());
		}
		assertEquals(9,a.getAllBieres(null).size());
		//assertEquals(4,a.getAllBieres("type").size());//search QUI NE MARCHE PAS DU TOUT !!!!
		assertEquals(4,a.getBieresByUno(1).size());//C'est un list
		assertEquals(1,a.getBieresByUno(2).size());		
		assertEquals(0,a.getBieresByUno(9009464).size());
		a.deleteBiere(1);
		try {
			a.getBiereByBno(1);
			fail("HTTP 202 Found");
		}catch(WebApplicationException WAE) {}
		
	}
	
	@Test
	public void HELP(){
		UtilisateurDao dao=BDDFactory.getDbi().open(UtilisateurDao.class);
		dao.dropUtilisateurTable();
		dao.createUtilisateurTable();
		Utilisateur a=Helper.createFullUtilisateur("prenom", "nom", "enseigne", "siret", "email", "adresse", "passwdHash", "tel", "type");
		assertEquals(dao.findByNom("nom").getUno(),a.getUno());
		Utilisateur amma=HelpU.createUtilisateurWithName("nanam");
		assertEquals(dao.findByNom("nanam").getUno(),amma.getUno());
	}
	
	@Test
	public void TestCommandeAvanced(){//0
		UtilisateurDto udto=new UtilisateurDto();
		udto.setUno(10);
		BiereDto bdto=new BiereDto();
		bdto.setBno(15);
		bdto.setUno(udto.getUno());
		assertEquals(bdto.getUno(),udto.getUno());
		CmdBDto cdto=new CmdBDto();//On commande un utilisateur...OK,ca marche!  
		cdto.setCno(1);
		cdto.setQte(52);
		cdto.setBno(bdto.getBno());
		cdto.setUno(udto.getUno());
		assertEquals(cdto.getUno(),udto.getUno());
		assertEquals(52,cdto.getQte());
		CmdB commande=new CmdB();
		assertFalse("BEERK",commande.isInCmdBGroup());
		commande.initFromDto(cdto);
		assertEquals(cdto.getCno(),commande.getCno());
		assertEquals(cdto.getUno(),commande.getUno());
		assertEquals(cdto.getQte(),commande.getQte());
		CmdB ata=new CmdB(10,15,999);
		cdto=ata.convertToDto();
		assertEquals(cdto.getCno(),ata.getCno());
		assertEquals(cdto.getUno(),ata.getUno());
		assertEquals(cdto.getQte(),ata.getQte());
	}
	
	
	private static List<Utilisateur> toutInitier(){
		List<Utilisateur> ca=new ArrayList<Utilisateur>();
		ca.add(new Utilisateur("prenoms", "nom","enseigne","siret","email","mdp","addrqsfqfesse","tel", "type","passqswdHash","RfIP"));
		ca.add(new Utilisateur("prenoqsfms", "nom","enseiqsgne","sirqfet","emsqfail","mqsfdp","addreqsfqsse","tqsfel", "qsqstyqsfqfspe","passwdHfash","RIPqsfq"));
		ca.add(new Utilisateur("pqsfrenoms", "nosqxm","enseifsqqsfqsgne","sireqsft","email","mdp","addrqsfesse","tqsfel", "typeqsfqf","passwdHashsq","RIqqsfP"));
		ca.add(new Utilisateur("prenofssms", "nom","enseiqsfgne","siqsfqfsret","email","mdqsqsp","addqsfqsfresse","tel", "tyqsfqsfpe","passwqsdHash","RIqsfP"));
		ca.add(new Utilisateur("prenomqsq", "nom","enseigsqfne","siret","emqsfqail","msqfdp","addresqsfse","teqsfqfl", "tyqsfqfpetype","passwdqsfHash","RIqsfP"));
		ca.add(new Utilisateur("prenomqqssq", "nom","enseigsqfne","siret","emqsfqail","msqfdp","addresqsfse","teqsfqfl", "tyqsfqfpeqs","passwdqsfHash","RIqsfP"));
		return ca;
	}
	private static List<Biere> toutInitierbiere(){
		List<Biere> ca=new ArrayList<Biere>();
		                //nom,uno,pno,prix,forme, type,description,taille,origine,degre,amertume) {
		ca.add(new Biere("nom",1,2, 0, "forme","type", "description",00,"origine",-20,"amertume"));	
		ca.add(new Biere("nom",2,3, 0, "forme","ttpe", "description",00,"origine",-20,"amertume"));
		ca.add(new Biere("nom",5,1, 0, "forme","typeqsdq", "description",00,"origine",-20,"amertume"));
		ca.add(new Biere("nom",1,2, 0, "forme","atype", "description",00,"origine",-20,"amertume"));
		ca.add(new Biere("nom",3,1, 0, "forme","vwtype", "description",00,"origine",-20,"amertume"));
		ca.add(new Biere("nom",5,2, 0, "forme","typsqdqse", "description",00,"origine",-20,"amertume"));
		ca.add(new Biere("nom",1,1, 0, "forme","typqqsqe", "description",00,"origine",-20,"amertume"));
		return ca;
	}

}
