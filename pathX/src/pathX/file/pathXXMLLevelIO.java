package pathX.file;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mini_game.SpriteType;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pathX.data.Bandit;
import pathX.data.Intersection;
import pathX.data.Police;
import pathX.data.Road;
import pathX.data.Zombie;
import pathX.data.pathXDataModel;
import pathX.data.pathXLevel;
import pathX.pathX;
import xml_utilities.XMLUtilities;
import static pathX.pathXConstants.*;
import pathX.ui.pathXMiniGame;
import pathX.ui.pathXTileState;
import properties_manager.PropertiesManager;
import pathX.pathX.pathXPropertyType;
/**
 *
 * @author Dell
 */
public class pathXXMLLevelIO
{
    // THIS WILL HELP US PARSE THE XML FILES
    private XMLUtilities xmlUtil;
    
    // THIS IS THE SCHEMA WE'LL USE
    private File levelSchema;
    
    private pathXMiniGame game;

    /**
     * Constructor for making our importer/exporter. Note that it
     * initializes the XML utility for processing XML files and it
     * sets up the schema for use.
     */
    public pathXXMLLevelIO(File initLevelSchema, pathXMiniGame initGame)
    {
        // THIS KNOWS HOW TO READ AND ACCESS XML FILES
        xmlUtil = new XMLUtilities();
        
        // WE'LL USE THE SCHEMA FILE TO VALIDATE THE XML FILES
        levelSchema = initLevelSchema;
        
        game = initGame;
    }
    
    /**
     * Reads the level data found in levelFile into levelToLoad.
     */
    public boolean loadLevel(File levelFile, pathXDataModel model)
    {
        try
        {
            // WE'LL FILL IN SOME OF THE LEVEL OURSELVES
            pathXLevel levelToLoad = model.getLevel();
            levelToLoad.reset();
            
            // FIRST LOAD ALL THE XML INTO A TREE
            Document doc = xmlUtil.loadXMLDocument( levelFile.getAbsolutePath(), 
                                                    levelSchema.getAbsolutePath());
            
            // FIRST LOAD THE LEVEL INFO
            Node levelNode = doc.getElementsByTagName(LEVEL_NODE).item(0);
            NamedNodeMap attributes = levelNode.getAttributes();
            String levelName = attributes.getNamedItem(NAME_ATT).getNodeValue();
            levelToLoad.setLevelName(levelName);
            String bgImageName = attributes.getNamedItem(IMAGE_ATT).getNodeValue();
            model.updateBackgroundImage(bgImageName);

            // THEN LET'S LOAD THE LIST OF ALL THE REGIONS
            loadIntersectionsList(doc, levelToLoad);
            ArrayList<Intersection> intersections = levelToLoad.getIntersections();
            
            loadZombiesList(doc, levelToLoad);
            loadPolicesList(doc, levelToLoad);
            loadBanditsList(doc, levelToLoad);
            
            // AND NOW CONNECT ALL THE REGIONS TO EACH OTHER
            loadRoadsList(doc, levelToLoad);
            
            // LOAD THE START INTERSECTION
            Node startIntNode = doc.getElementsByTagName(START_INTERSECTION_NODE).item(0);
            attributes = startIntNode.getAttributes();
            String startIdText = attributes.getNamedItem(ID_ATT).getNodeValue();
            int startId = Integer.parseInt(startIdText);
            String startImageName = attributes.getNamedItem(IMAGE_ATT).getNodeValue();
            Intersection startingIntersection = intersections.get(startId);
            levelToLoad.setStartingLocation(startingIntersection);
            model.updateStartingLocationImage(startImageName);
            
            // LOAD THE DESTINATION
            Node destIntNode = doc.getElementsByTagName(DESTINATION_INTERSECTION_NODE).item(0);
            attributes = destIntNode.getAttributes();
            String destIdText = attributes.getNamedItem(ID_ATT).getNodeValue();
            int destId = Integer.parseInt(destIdText);
            String destImageName = attributes.getNamedItem(IMAGE_ATT).getNodeValue();
            levelToLoad.setDestination(intersections.get(destId));
            model.updateDestinationImage(destImageName);
            
            // LOAD THE MONEY
            Node moneyNode = doc.getElementsByTagName(MONEY_NODE).item(0);
            attributes = moneyNode.getAttributes();
            String moneyText = attributes.getNamedItem(AMOUNT_ATT).getNodeValue();
            int money = Integer.parseInt(moneyText);
            levelToLoad.setMoney(money);
            
            // LOAD THE NUMBER OF POLICE
            Node policeNode = doc.getElementsByTagName(POLICE_NODE).item(0);
            attributes = policeNode.getAttributes();
            String policeText = attributes.getNamedItem(NUM_ATT).getNodeValue();
            int numPolice = Integer.parseInt(policeText);
            levelToLoad.setNumPolice(numPolice);
            
            // LOAD THE NUMBER OF BANDITS
            Node banditsNode = doc.getElementsByTagName(BANDITS_NODE).item(0);
            attributes = banditsNode.getAttributes();
            String banditsText = attributes.getNamedItem(NUM_ATT).getNodeValue();
            int numBandits = Integer.parseInt(banditsText);
            levelToLoad.setNumBandits(numBandits);
            
            // LOAD THE NUMBER OF ZOMBIES
            Node zombiesNode = doc.getElementsByTagName(ZOMBIES_NODE).item(0);
            attributes = zombiesNode.getAttributes();
            String zombiesText = attributes.getNamedItem(NUM_ATT).getNodeValue();
            int numZombies = Integer.parseInt(zombiesText);
            levelToLoad.setNumZombies(numZombies);            
        }
        catch(Exception e)
        {
            // LEVEL DIDN'T LOAD PROPERLY
            return false;
        }
        // LEVEL LOADED PROPERLY
        return true;
    }
    
    // PRIVATE HELPER METHOD FOR LOADING INTERSECTIONS INTO OUR LEVEL
    private void loadIntersectionsList( Document doc, pathXLevel levelToLoad)
    {
        // FIRST GET THE REGIONS LIST
        Node intersectionsListNode = doc.getElementsByTagName(INTERSECTIONS_NODE).item(0);
        ArrayList<Intersection> intersections = levelToLoad.getIntersections();
        
        // AND THEN GO THROUGH AND ADD ALL THE LISTED REGIONS
        ArrayList<Node> intersectionsList = xmlUtil.getChildNodesWithName(intersectionsListNode, INTERSECTION_NODE);
        for (int i = 0; i < intersectionsList.size(); i++)
        {
            // GET THEIR DATA FROM THE DOC
            Node intersectionNode = intersectionsList.get(i);
            NamedNodeMap intersectionAttributes = intersectionNode.getAttributes();
            String idText = intersectionAttributes.getNamedItem(ID_ATT).getNodeValue();
            String openText = intersectionAttributes.getNamedItem(OPEN_ATT).getNodeValue();
            String xText = intersectionAttributes.getNamedItem(X_ATT).getNodeValue();
            int x = Integer.parseInt(xText) + GAME_OFFSET;
            String yText = intersectionAttributes.getNamedItem(Y_ATT).getNodeValue();
            int y = Integer.parseInt(yText);
            
            // NOW MAKE AND ADD THE INTERSECTION
            Intersection newIntersection = new Intersection(x, y);
            newIntersection.open = Boolean.parseBoolean(openText);
            intersections.add(newIntersection);
        }
    }
    
    // PRIVATE HELPER METHOD FOR LOADING ZOMBIES INTO OUR LEVEL
    private void loadZombiesList(Document doc, pathXLevel levelToLoad)
    {
        Node zombiesListNode = doc.getElementsByTagName(ZOMBIE_LIST_NODE).item(0);
        ArrayList<Zombie> zombies = levelToLoad.getZombies();
        SpriteType zT = new SpriteType(ZOMBIE_TYPE);
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(pathXPropertyType.PATH_IMG);
        BufferedImage img = game.loadImage(imgPath + props.getProperty(pathXPropertyType.IMAGE_ZOMBIE));
        zT.addState(pathXTileState.VISIBLE_STATE.toString(), img);
        
        ArrayList<Node> zombiesList = xmlUtil.getChildNodesWithName(zombiesListNode, ZOMBIE_NODE);
        for (int i = 0; i < zombiesList.size(); i++)
        {
            // GET THEIR DATA FROM THE DOC
            Node zombieNode = zombiesList.get(i);
            NamedNodeMap zombieAttributes = zombieNode.getAttributes();
            String idText = zombieAttributes.getNamedItem(ID_ATT).getNodeValue();
            String xText = zombieAttributes.getNamedItem(X_ATT).getNodeValue();
            int x = Integer.parseInt(xText) + GAME_OFFSET;
            String yText = zombieAttributes.getNamedItem(Y_ATT).getNodeValue();
            int y = Integer.parseInt(yText);
            
            Zombie newZombie = new Zombie(zT, (float) x, (float) y, 0, 0, pathXTileState.VISIBLE_STATE.toString());
            zombies.add(newZombie);
        }
    }
    
    // PRIVATE HELPER METHOD FOR LOADING POLICE INTO OUR LEVEL
    private void loadPolicesList(Document doc, pathXLevel levelToLoad)
    {
        Node policeListNode = doc.getElementsByTagName(POLICE_LIST_NODE).item(0);
        ArrayList<Police> polices = levelToLoad.getPolices();
        SpriteType pT = new SpriteType(POLICE_TYPE);
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(pathXPropertyType.PATH_IMG);
        BufferedImage img = game.loadImage(imgPath + props.getProperty(pathXPropertyType.IMAGE_POLICE));
        pT.addState(pathXTileState.VISIBLE_STATE.toString(), img);
        
        ArrayList<Node> policeList = xmlUtil.getChildNodesWithName(policeListNode, POLICE_NODE2);
        for (int i = 0; i < policeList.size(); i++)
        {
            // GET THEIR DATA FROM THE DOC
            Node policeNode = policeList.get(i);
            NamedNodeMap policeAttributes = policeNode.getAttributes();
            String idText = policeAttributes.getNamedItem(ID_ATT).getNodeValue();
            String xText = policeAttributes.getNamedItem(X_ATT).getNodeValue();
            int x = Integer.parseInt(xText) + GAME_OFFSET;
            String yText = policeAttributes.getNamedItem(Y_ATT).getNodeValue();
            int y = Integer.parseInt(yText);
            
            Police newPolice = new Police(pT, (float) x, (float) y, 0, 0, pathXTileState.VISIBLE_STATE.toString());
            polices.add(newPolice);
        }
    }
    
    // PRIVATE HELPER METHOD FOR LOADING BANDITS INTO OUR LEVEL
    private void loadBanditsList(Document doc, pathXLevel levelToLoad)
    {
        Node banditListNode = doc.getElementsByTagName(BANDIT_LIST_NODE).item(0);
        ArrayList<Bandit> bandits = levelToLoad.getBandits();
        SpriteType bT = new SpriteType(BANDIT_TYPE);
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(pathXPropertyType.PATH_IMG);
        BufferedImage img = game.loadImage(imgPath + props.getProperty(pathXPropertyType.IMAGE_BANDIT));
        bT.addState(pathXTileState.VISIBLE_STATE.toString(), img);
        
        ArrayList<Node> banditList = xmlUtil.getChildNodesWithName(banditListNode, BANDIT_NODE);
        for (int i = 0; i < banditList.size(); i++)
        {
            // GET THEIR DATA FROM THE DOC
            Node banditNode = banditList.get(i);
            NamedNodeMap banditAttributes = banditNode.getAttributes();
            String idText = banditAttributes.getNamedItem(ID_ATT).getNodeValue();
            String xText = banditAttributes.getNamedItem(X_ATT).getNodeValue();
            int x = Integer.parseInt(xText) + GAME_OFFSET;
            String yText = banditAttributes.getNamedItem(Y_ATT).getNodeValue();
            int y = Integer.parseInt(yText);
            
            Bandit newBandit = new Bandit(bT, (float) x, (float) y, 0, 0, pathXTileState.VISIBLE_STATE.toString());
            bandits.add(newBandit);
        }
    }

    // PRIVATE HELPER METHOD FOR LOADING ROADS INTO OUR LEVEL
    private void loadRoadsList( Document doc, pathXLevel levelToLoad)
    {
        // FIRST GET THE REGIONS LIST
        Node roadsListNode = doc.getElementsByTagName(ROADS_NODE).item(0);
        ArrayList<Road> roads = levelToLoad.getRoads();
        ArrayList<Intersection> intersections = levelToLoad.getIntersections();
        
        // AND THEN GO THROUGH AND ADD ALL THE LISTED REGIONS
        ArrayList<Node> roadsList = xmlUtil.getChildNodesWithName(roadsListNode, ROAD_NODE);
        for (int i = 0; i < roadsList.size(); i++)
        {
            // GET THEIR DATA FROM THE DOC
            Node roadNode = roadsList.get(i);
            NamedNodeMap roadAttributes = roadNode.getAttributes();
            String id1Text = roadAttributes.getNamedItem(INT_ID1_ATT).getNodeValue();
            int int_id1 = Integer.parseInt(id1Text);
            String id2Text = roadAttributes.getNamedItem(INT_ID2_ATT).getNodeValue();
            int int_id2 = Integer.parseInt(id2Text);
            String oneWayText = roadAttributes.getNamedItem(ONE_WAY_ATT).getNodeValue();
            boolean oneWay = Boolean.parseBoolean(oneWayText);
            String speedLimitText = roadAttributes.getNamedItem(SPEED_LIMIT_ATT).getNodeValue();
            int speedLimit = Integer.parseInt(speedLimitText);
            
            // NOW MAKE AND ADD THE ROAD
            Road newRoad = new Road();
            newRoad.setNode1(intersections.get(int_id1));
            newRoad.setNode2(intersections.get(int_id2));
            newRoad.setOneWay(oneWay);
            newRoad.setOpen(true);
            newRoad.setSpeedLimit(speedLimit);
            newRoad.calculateDistance();
            roads.add(newRoad);
        }
    }
    
    /**
     * This method saves the level currently being edited to the levelFile. Note
     * that it will be saved as an .xml file, which is an XML-format that will
     * conform to the schema.
     */
    public boolean saveLevel(File levelFile, pathXLevel levelToSave)
    {
        try 
        {
            // THESE WILL US BUILD A DOC
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            // FIRST MAKE THE DOCUMENT
            Document doc = docBuilder.newDocument();
            
            // THEN THE LEVEL (i.e. THE ROOT) ELEMENT
            Element levelElement = doc.createElement(LEVEL_NODE);
            doc.createAttribute(NAME_ATT);
            levelElement.setAttribute(NAME_ATT, levelToSave.getLevelName());
            doc.appendChild(levelElement);
            doc.createAttribute(IMAGE_ATT);
            levelElement.setAttribute(IMAGE_ATT, levelToSave.getBackgroundImageFileName());
 
            // THEN THE INTERSECTIONS
            Element intersectionsElement = makeElement(doc, levelElement, INTERSECTIONS_NODE, "");
            
            // AND LET'S ADD EACH INTERSECTION
            int id = 0;
            doc.createAttribute(ID_ATT); 
            doc.createAttribute(X_ATT);
            doc.createAttribute(Y_ATT);
            doc.createAttribute(OPEN_ATT);
            for (Intersection i : levelToSave.getIntersections())
            {
                // MAKE AN INTERSECTION NODE AND ADD IT
                Element intersectionNodeElement = makeElement(doc, intersectionsElement,
                        INTERSECTION_NODE, "");
                
                // NOW LET'S FILL IN THE INTERSECTION'S DATA. FIRST MAKE THE ATTRIBUTES
                intersectionNodeElement.setAttribute(ID_ATT,    "" + id);
                intersectionNodeElement.setAttribute(X_ATT,     "" + i.x);
                intersectionNodeElement.setAttribute(Y_ATT,     "" + i.y);
                intersectionNodeElement.setAttribute(OPEN_ATT,  "" + i.open);
             }

            // AND NOW ADD ALL THE ROADS
            Element roadsElement = makeElement(doc, levelElement, ROADS_NODE, "");
            doc.createAttribute(INT_ID1_ATT);
            doc.createAttribute(INT_ID2_ATT);
            doc.createAttribute(SPEED_LIMIT_ATT);
            doc.createAttribute(ONE_WAY_ATT);
            ArrayList<Intersection> intersections = levelToSave.getIntersections();
            for (Road r : levelToSave.getRoads())
            {
                // MAKE A ROAD NODE AND ADD IT TO THE LIST
                Element roadNodeElement = makeElement(doc, roadsElement, ROAD_NODE, "");
                int intId1 = intersections.indexOf(r.getNode1());
                roadNodeElement.setAttribute(INT_ID1_ATT, "" + intId1);
                int intId2 = intersections.indexOf(r.getNode2());
                roadNodeElement.setAttribute(INT_ID2_ATT, "" + intId2);
                roadNodeElement.setAttribute(SPEED_LIMIT_ATT, "" + r.getSpeedLimit());
                roadNodeElement.setAttribute(ONE_WAY_ATT, "" + r.isOneWay());
            }
            
            // NOW THE START INTERSECTION
            Element startElement = makeElement(doc, levelElement, START_INTERSECTION_NODE, "");
            int startId = intersections.indexOf(levelToSave.getStartingLocation());
            startElement.setAttribute(ID_ATT, "" + startId);
            startElement.setAttribute(IMAGE_ATT, levelToSave.getStartingLocationImageFileName());
            
            // AND THE DESTINATION
            Element destElement = makeElement(doc, levelElement, DESTINATION_INTERSECTION_NODE, "");
            int destId = intersections.indexOf(levelToSave.getDestination());
            destElement.setAttribute(ID_ATT, "" + destId);
            destElement.setAttribute(IMAGE_ATT, levelToSave.getDestinationImageFileName());
            
            // NOW THE MONEY
            Element moneyElement = makeElement(doc, levelElement, MONEY_NODE, "");
            doc.createAttribute(AMOUNT_ATT);
            moneyElement.setAttribute(AMOUNT_ATT, "" + levelToSave.getMoney());
            
            // AND THE POLICE COUNT
            Element policeElement = makeElement(doc, levelElement, POLICE_NODE, "");
            doc.createAttribute(NUM_ATT);
            policeElement.setAttribute(NUM_ATT, "" + levelToSave.getNumPolice());
            
            // AND THE BANDIT COUNT
            Element banditElement = makeElement(doc, levelElement, BANDITS_NODE, "");
            banditElement.setAttribute(NUM_ATT, "" + levelToSave.getNumBandits());
            
            // AND FINALLY THE ZOMBIES COUNT
            Element zombiesElement = makeElement(doc, levelElement, ZOMBIES_NODE, "");
            zombiesElement.setAttribute(NUM_ATT, "" + levelToSave.getNumZombies());

            // THE TRANSFORMER KNOWS HOW TO WRITE A DOC TO
            // An XML FORMATTED FILE, SO LET'S MAKE ONE
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, YES_VALUE);
            transformer.setOutputProperty(XML_INDENT_PROPERTY, XML_INDENT_VALUE);
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(levelFile);
            
            // SAVE THE POSE TO AN XML FILE
            transformer.transform(source, result);    

            // SUCCESS
            return true;
        }
        catch(TransformerException | ParserConfigurationException | DOMException | HeadlessException ex)
        {
            // SOMETHING WENT WRONG
            return false;
        }    
    }   
    
    // THIS HELPER METHOD BUILDS ELEMENTS (NODES) FOR US TO HELP WITH
    // BUILDING A Doc WHICH WE WOULD THEN SAVE TO A FILE.
    private Element makeElement(Document doc, Element parent, String elementName, String textContent)
    {
        Element element = doc.createElement(elementName);
        element.setTextContent(textContent);
        parent.appendChild(element);
        return element;
    }
}