set sqlblanklines off;
DECLARE 
existe_Creneau INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_Creneau FROM user_tables WHERE table_name = upper('Creneau') ;    
IF existe_Creneau > 0 THEN 
EXECUTE IMMEDIATE 'DROP TABLE Creneau CASCADE CONSTRAINTS' ;    
END IF ;  
END ;
/
CREATE TABLE Creneau(creneauxId_Creneau INT NOT NULL, datedebut_Creneau TIMESTAMP, datefin_Creneau TIMESTAMP, PRIMARY KEY (creneauxId_Creneau)) ;
DECLARE 
existe_SeqCreneau INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_SeqCreneau FROM all_sequences WHERE SEQUENCE_NAME = upper('SEQ_CRENEAU') ;    
IF existe_SeqCreneau > 0 THEN 
EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_Creneau' ;    
END IF ;  
END ;
/
CREATE SEQUENCE SEQ_Creneau ;
CREATE TRIGGER TRIG_Creneau BEFORE INSERT ON Creneau FOR EACH ROW 
BEGIN 
SELECT SEQ_Creneau.NEXTVAL INTO :new.creneauxId_Creneau FROM DUAL ;   
END ; 
/
DECLARE 
existe_Patient INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_Patient FROM user_tables WHERE table_name = upper('Patient') ;    
IF existe_Patient > 0 THEN 
EXECUTE IMMEDIATE 'DROP TABLE Patient CASCADE CONSTRAINTS' ;    
END IF ;  
END ; 
/
CREATE TABLE Patient (patientId_Patient INT NOT NULL, nom_Patient VARCHAR(60) NOT NULL, prenom_Patient VARCHAR(60) NOT NULL, email_Patient VARCHAR(60) NOT NULL, mot_de_passe_Patient VARCHAR(60) NOT NULL, date_de_naissance_Patient DATE NOT NULL, moyenId_Moyen INT, PRIMARY KEY (patientId_Patient)); 
DECLARE 
existe_SeqPatient INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_SeqPatient FROM all_sequences WHERE SEQUENCE_NAME = upper('SEQ_PATIENT') ;    
IF existe_SeqPatient > 0 THEN 
EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_Patient' ;    
END IF ;  
END ;
/
CREATE SEQUENCE SEQ_Patient ; 
CREATE TRIGGER TRIG_Patient BEFORE INSERT ON Patient FOR EACH ROW 
BEGIN 
SELECT SEQ_Patient.NEXTVAL INTO :new.patientId_Patient FROM DUAL ;   
END ;  
/
DECLARE 
existe_Profession INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_Profession FROM user_tables WHERE table_name = upper('Profession') ;    
IF existe_Profession > 0 THEN 
EXECUTE IMMEDIATE 'DROP TABLE Profession CASCADE CONSTRAINTS' ;    
END IF ;  
END ; 
/
CREATE TABLE Profession (professsionId_Profession INT NOT NULL, professionNom_Profession VARCHAR(60), PRIMARY KEY (professsionId_Profession)); 
DECLARE 
existe_SeqProfession INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_SeqProfession FROM all_sequences WHERE SEQUENCE_NAME = upper('SEQ_PROFESSION') ;    
IF existe_SeqProfession > 0 THEN 
EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_Profession' ;    
END IF ;  
END ;
/
CREATE SEQUENCE SEQ_Profession ; 
CREATE TRIGGER TRIG_Profession BEFORE INSERT ON Profession FOR EACH ROW 
BEGIN 
SELECT SEQ_Profession.NEXTVAL INTO :new.professsionId_Profession FROM DUAL ;   
END ;  
/
DECLARE 
existe_Moyen INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_Moyen FROM user_tables WHERE table_name = upper('Moyen') ;    
IF existe_Moyen > 0 THEN 
EXECUTE IMMEDIATE 'DROP TABLE Moyen CASCADE CONSTRAINTS' ;    
END IF ;  
END ; 
/
CREATE TABLE Moyen (moyenId_Moyen INT NOT NULL, moyenNom_Moyen VARCHAR(60), PRIMARY KEY (moyenId_Moyen)); 
DECLARE 
existe_SeqMoyen INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_SeqMoyen FROM all_sequences WHERE SEQUENCE_NAME = upper('SEQ_MOYEN') ;    
IF existe_SeqMoyen > 0 THEN 
EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_Moyen' ;    
END IF ;  
END ;
/
CREATE SEQUENCE SEQ_Moyen ; 
CREATE TRIGGER TRIG_Moyen BEFORE INSERT ON Moyen FOR EACH ROW 
BEGIN 
SELECT SEQ_Moyen.NEXTVAL INTO :new.moyenId_Moyen FROM DUAL ;   
END ;  
/
DECLARE 
existe_CONSULTATION INTEGER ;  
BEGIN 
SELECT count(*) INTO existe_CONSULTATION FROM user_tables WHERE table_name = upper('CONSULTATION') ;   
IF existe_CONSULTATION > 0 THEN 
EXECUTE IMMEDIATE 'DROP TABLE CONSULTATION CASCADE CONSTRAINTS' ;    
END IF ;  
END ; 
/
CREATE TABLE CONSULTATION (creneauxId_Creneau INT NOT NULL,patientId_Patient INT NOT NULL, prix_CONSULTATION INT, typeReglement_CONSULTATION VARCHAR(60), posture_CONSULTATION VARCHAR(60), mot_clef_CONSULTATION VARCHAR(60), comportement_CONSULTATION VARCHAR(60), classification_CONSULTATION VARCHAR(60), noteAnxiete_CONSULTATION INT, dateReglement_CONSULTATION TIMESTAMP, PRIMARY KEY (creneauxId_Creneau,  patientId_Patient)); 
DECLARE 
existe_EMPLOI INTEGER ; 
BEGIN 
SELECT count(*) INTO existe_EMPLOI FROM user_tables WHERE table_name = upper('EMPLOI') ;    
IF existe_EMPLOI > 0 THEN 
EXECUTE IMMEDIATE 'DROP TABLE EMPLOI CASCADE CONSTRAINTS' ;    
END IF ;  
END ; 
/
CREATE TABLE EMPLOI (patientId_Patient INT NOT NULL, professsionId_Profession INT NOT NULL, debut_Profession DATE, in_Profession DATE, PRIMARY KEY (patientId_Patient,  professsionId_Profession)); 
ALTER TABLE Patient ADD CONSTRAINT FK_Patient_moyenId_Moyen FOREIGN KEY (moyenId_Moyen) REFERENCES Moyen (moyenId_Moyen); 
ALTER TABLE CONSULTATION ADD CONSTRAINT FK_CONSULTATION_creneauxId FOREIGN KEY (creneauxId_Creneau) REFERENCES Creneau (creneauxId_Creneau); 
ALTER TABLE CONSULTATION ADD CONSTRAINT FK_CONSULTATION_patientId FOREIGN KEY (patientId_Patient) REFERENCES Patient (patientId_Patient); 
ALTER TABLE EMPLOI ADD CONSTRAINT FK_EMPLOI_patientId_Patient FOREIGN KEY (patientId_Patient) REFERENCES Patient (patientId_Patient); 
ALTER TABLE EMPLOI ADD CONSTRAINT FK_EMPLOI_professsionId FOREIGN KEY (professsionId_Profession) REFERENCES Profession (professsionId_Profession);
