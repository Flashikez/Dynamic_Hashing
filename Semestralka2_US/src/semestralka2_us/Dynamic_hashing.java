/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us;

import java.io.BufferedReader;
import java.io.File;
import semestralka2_us.Util.Utilities;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import semestralka2_us.nodes.ExternalNode;
import semestralka2_us.nodes.InternalNode;
import semestralka2_us.nodes.Node;
import semestralka2_us.nodes.ReturnNode_Depth;

/**
 * Dynamické hashovanie 
 *
 * @author MarekPC
 */
public class Dynamic_hashing {

    private FreeBlocksManager dataFileManager;
    private FreeBlocksManager overFillFileManager;

    private RandomAccessFile dataFile;
    private RandomAccessFile overFillFile;
    private Node root;
    private Record exampleInstance;

    private int maxHashBit;

    private int dataBlockFactor;
    private int overFillBlockFactor;

    /**
     * Konštruktor pre čistý štart
     *
     * @param <T>
     * @param dataFile
     * @param overFillFile
     * @param hashBitSize
     * @param dataBlockFactor
     * @param overFillBlockFactor
     * @param recordClass
     * @throws FileNotFoundException
     * @throws IOException
     */
    public <T extends Record> Dynamic_hashing(String dataFile, String overFillFile, int hashBitSize, int dataBlockFactor, int overFillBlockFactor, Class<T> recordClass) throws FileNotFoundException, IOException {
        createFiles(dataFile, overFillFile);
        try {
            this.exampleInstance = recordClass.newInstance();
        } catch (InstantiationException ex) {
            System.err.println("Add empty construcor as first constructor declaration!");
            Logger.getLogger(Dynamic_hashing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.err.println("Add empty construcor as first constructor declaration!");
            Logger.getLogger(Dynamic_hashing.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.maxHashBit = hashBitSize - 1;
        this.dataBlockFactor = dataBlockFactor;
        this.overFillBlockFactor = overFillBlockFactor;
        freshStart();
    }

    /**
     * Konštuktor pre load zo súboru
     *
     * @param <T>
     * @param dataFile
     * @param overFillFile
     * @param dataManager
     * @param overFillManager
     * @param dataBlockFactor
     * @param overFillBlockFactor
     * @param maxHashBit
     * @param root
     * @param recordClass
     */
    private <T extends Record> Dynamic_hashing(RandomAccessFile dataFile, RandomAccessFile overFillFile, FreeBlocksManager dataManager, FreeBlocksManager overFillManager, int dataBlockFactor, int overFillBlockFactor, int maxHashBit, Node root, Class<T> recordClass) {
        this.dataFile = dataFile;
        try {
            this.exampleInstance = recordClass.newInstance();
        } catch (InstantiationException ex) {
            System.err.println("Add empty constructor as first constructor declaration!");
            Logger.getLogger(Dynamic_hashing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.err.println("Add empty constructor as first constructor declaration!");
            Logger.getLogger(Dynamic_hashing.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.overFillFile = overFillFile;
        this.dataFileManager = dataManager;
        this.overFillFileManager = overFillManager;
        this.root = root;
        this.maxHashBit = maxHashBit;
        this.dataBlockFactor = dataBlockFactor;
        this.overFillBlockFactor = overFillBlockFactor;

    }

    private void createFiles(String dataFile, String overFillFile) throws FileNotFoundException {
        this.dataFile = new RandomAccessFile(dataFile, "rw");
        this.overFillFile = new RandomAccessFile(overFillFile, "rw");

    }

    private void freshStart() throws IOException {
        this.dataFile.setLength(0);
        this.overFillFile.setLength(0);
        this.root = new ExternalNode(null, 0, -1);
        this.dataFileManager = new FreeBlocksManager(dataFile, DataBlock.getByteArraySize(dataBlockFactor, exampleInstance.getClass()));
        this.overFillFileManager = new FreeBlocksManager(overFillFile, DataBlock.getByteArraySize(overFillBlockFactor, exampleInstance.getClass()));

//        createEmptyBlock(0, dataBlockFactor);
    }

    
    /**
     * Kontrola či záznam sa už v hashovaní nachádza
     * @param record
     * @param block
     * @return
     * @throws IOException 
     */
    private boolean containsRecord(Record record, DataBlock block) throws IOException {

        while (true) {
            Record searched = block.getRecord(record);
            if (searched != null) {
                return true;
            } else if (block.getOverFill_Offset() != -1) {
                block = loadOverFillBlock(block.getOverFill_Offset(), overFillBlockFactor);
            } else {
                break;
            }
        }

        return false;

    }

    /**
     * Pridá rekord do dynamického hashovania
     *
     * @param record
     * @return true - ak záznam bol pridaný
     * @throws IOException
     */
    public boolean insert(Record record) throws IOException {

        BitSet recordHash = record.getHash();
        ReturnNode_Depth node_depth = getNode(recordHash);
        ExternalNode current = (ExternalNode) node_depth.node;

            // Externá ale nealokovaná
        if (current.getFileOffset() == -1) {
            

            DataBlock newBlock = new DataBlock(dataBlockFactor);
            newBlock.addRecord(record);
            long newOffset = dataFileManager.getFreeBlock();
            if (newOffset == -1) {
                newOffset = dataFile.length();
            }
            current.setFileOffset(newOffset);
            writeDataBlock(newOffset, newBlock);
            current.addToCount(1);
            return true;

        }
        DataBlock block = loadDataBlock(current.getFileOffset(), dataBlockFactor);
        if (containsRecord(record, block)) {
            return false;
        }
        // SIZE OK
        if (current.getValidCount() < dataBlockFactor) {
            block.addRecord(record);
            writeDataBlock(current.getFileOffset(), block);
            current.addToCount(1);
            return true;

        }
        // FULL
        int currentDepth = maxHashBit - node_depth.depth;
        long currentOffset = current.getFileOffset();

        /* Pamatanie cesty aby sme sa vedeli vrátiť v prípade že sa blok nedá rozdeliť */
        ExternalNode colisionNode = current;
        Node parentOfColisionNode = colisionNode.parent;
        boolean colisionLeftOfParent = false;
        if (parentOfColisionNode != null) {
            if (parentOfColisionNode.left == colisionNode) {
                colisionLeftOfParent = true;
            }
        }
        // ******Cyklus rozdelovania bloku podla hash bitu*****

        while (currentDepth >= 0) {

            if (block.getOverFill_Offset() != -1) {
                break;
            }

            boolean rightPath = false;

            // Skús rekordy rozdelit podla bitu ich hashu 
            ArrayList<Record>[] blockDivided = block.divideRecordsOnHashBit(currentDepth, record);
            ArrayList<Record> leftRecords = blockDivided[0];
            ArrayList<Record> rightRecords = blockDivided[1];

            
            if (leftRecords.size() > 0 && rightRecords.size() > 0) {
                // Blok sa podarilo rozdelit
                DataBlock leftBlock = new DataBlock(dataBlockFactor);
                for (Record leftRecord : leftRecords) {
                    leftBlock.addRecord(leftRecord);

                }
                DataBlock rightBlock = new DataBlock(dataBlockFactor);
                for (Record rightRecord : rightRecords) {
                    rightBlock.addRecord(rightRecord);
                }

                // vyuzi volny offset ak existuje
                long newOffset = dataFileManager.getFreeBlock();
                if (newOffset == -1) {
                    newOffset = dataFile.length();
                }

                // Vytvor 2 nové externé vrcholy a prirad im bloky
                Node parent = current.parent;

                InternalNode internal = new InternalNode(parent, null, null);

                ExternalNode leftNode = new ExternalNode(internal, leftRecords.size(), currentOffset);
                ExternalNode rightNode = new ExternalNode(internal, rightRecords.size(), newOffset);
                writeDataBlock(newOffset, rightBlock);
                writeDataBlock(currentOffset, leftBlock);
                internal.setLeft(leftNode);
                internal.setRight(rightNode);

                if (parent == null) {
                    this.root = internal;
                } else if (parent.left == current) {
                    parent.left = internal;
                } else {
                    parent.right = internal;
                }

                return true;

            }

            rightPath = leftRecords.size() > rightRecords.size() ? false : true;

            // Externy na interny
            Node parent = current.parent;
            InternalNode internal = new InternalNode(parent, null, null);

            ExternalNode externalLeft = new ExternalNode(internal, 0, -1);
            ExternalNode externalRight = new ExternalNode(internal, 0, -1);
            internal.setLeft(externalLeft);
            internal.setRight(externalRight);

            if (parent != null) {
                if (parent.right == current) {
                    parent.right = internal;
                } else {
                    parent.left = internal;
                }

            } else {
                root = internal;
            }

            if (rightPath) {
                current = externalRight;
            } else {
                current = externalLeft;
            }
            currentDepth--;
        }
        // ************************ Kolízia ************************
        // "Rollback" vytvrených nodov pri skúšaní delenia bloku
        if (parentOfColisionNode == null) {
            this.root = colisionNode;
        } else if (colisionLeftOfParent) {
            parentOfColisionNode.left = colisionNode;
        } else {
            parentOfColisionNode.right = colisionNode;
        }
        // Pridaj do zretazenia
        addToColisionChain(block, colisionNode, record);
        return true;

    }

    /**
     * Skontroluje atribút overFillChainLength všettkým dátovým blokom so
     * skutočnou hodnotou (LEN NA TESTING)
     *
     * @return
     * @throws IOException
     */
    public boolean chainLengthCheck() throws IOException {
        ArrayList<ExternalNode> nodes = getInOrder();

        for (ExternalNode node : nodes) {
            DataBlock dataBlock = loadDataBlock(node.getFileOffset(), dataBlockFactor);
            ArrayList<DataBlock> chain = getOverFillChain(dataBlock);
            if (dataBlock.getOverFill_ChainLength() != chain.size() - 1) {
                return false;

            }

        }
        return true;
    }

    /**
     * Pridá nový rekord do zreťazenia, rieši kolízie
     *
     * @param dataBlock dátovy blok
     * @param colisionNode externý vrchol Trie-u
     * @param record rekord na pridanie
     * @throws IOException
     */
    private void addToColisionChain(DataBlock dataBlock, ExternalNode colisionNode, Record record) throws IOException {
        long firstOffset = dataBlock.getOverFill_Offset();
        //Nemá preplňujúci
        if (firstOffset == -1) {
            
            DataBlock overFillBlock = new DataBlock(overFillBlockFactor);
            overFillBlock.addRecord(record);
            colisionNode.addToCount(1);

            // Výuží volný blok ak exstiuje
            long fileOffset = overFillFileManager.getFreeBlock();
            if (fileOffset == -1) {
                fileOffset = overFillFile.length();
            }
            dataBlock.setOverFill_Offset(fileOffset);
            dataBlock.setOverFill_ChainLength(1);

            writeOverFillBlock(fileOffset, overFillBlock);
            writeDataBlock(colisionNode.getFileOffset(), dataBlock);
            return;
        }
        // má preplňujúci
        // nájdi prvé volbé miesto v zreťazení vrátane dátoveho bloku
        DataBlock current = dataBlock;
        long currentOffset = colisionNode.getFileOffset();
        while (true) {

            if (current.getValidCount() < current.getBlockFactor()) {
                current.addRecord(record);
                colisionNode.addToCount(1);
                if (current == dataBlock) {
                    writeDataBlock(currentOffset, current);
                } else {
                    writeOverFillBlock(currentOffset, current);
                }
                return;

            }
            if (current.getOverFill_Offset() == -1) {
                break;
            }

            currentOffset = current.getOverFill_Offset();

            current = loadOverFillBlock(currentOffset, overFillBlockFactor);

        }

        // Pridaj overFillBlock na koniec
        long newOffset = overFillFileManager.getFreeBlock();
        if (newOffset == -1) {
            newOffset = overFillFile.length();
        }
        colisionNode.addToCount(1);
        dataBlock.setOverFill_ChainLength(dataBlock.getOverFill_ChainLength() + 1);
        writeDataBlock(colisionNode.getFileOffset(), dataBlock);

        DataBlock newBlock = new DataBlock(overFillBlockFactor);
        newBlock.addRecord(record);
        writeOverFillBlock(newOffset, newBlock);
        current.setOverFill_Offset(newOffset);
        writeOverFillBlock(currentOffset, current);
        return;
    }

    public long getDataFileSize() throws IOException {
        return dataFile.length();
    }

    public long getOverfillFileSize() throws IOException {
        return overFillFile.length();
    }

    /**
     *  výpis dátového súboru
     *
     * @return
     * @throws IOException
     */
    public List<Block_String> getWholeDataFileAsStrings() throws IOException {

        List<Long> offsets = getInOrder().stream().map(e -> e.getFileOffset()).collect(Collectors.toList());
        List<Block_String> list = new ArrayList<>();
        int sizeOfBlock = DataBlock.getByteArraySize(dataBlockFactor, exampleInstance.getClass());
        for (int i = 0; i < dataFile.length() / sizeOfBlock; i++) {
            long offset = i * sizeOfBlock;
            if (offsets.contains(offset)) {
                DataBlock block = loadDataBlock(offset, dataBlockFactor);
                list.add(block.getBString(offset));
            } else {
                list.add(new Block_String(Long.toString(offset), "FREE BLOCK", "FREE BLOCK", "FREE BLOCK", new ArrayList<String>()));
            }

        }
        return list;
    }

    /**
     *  výpis preplňujúceho súboru
     *
     * @return
     * @throws IOException
     */
    public List<Block_String> getWholeOverfillFileAsStrings() throws IOException {
        List<ExternalNode> nodes = getInOrder();
        List<Long> offsets = new ArrayList<>();
        for (ExternalNode node : nodes) {
            DataBlock block = loadDataBlock(node.getFileOffset(), dataBlockFactor);
            ArrayList<DataBlock> chain = getOverFillChain(block);
            offsets.addAll(chain.stream().map(t -> t.getOverFill_Offset()).collect(Collectors.toList()));

        }

        List<Block_String> list = new ArrayList<>();
        int sizeOfBlock = DataBlock.getByteArraySize(overFillBlockFactor, exampleInstance.getClass());
        for (int i = 0; i < overFillFile.length() / sizeOfBlock; i++) {
            long offset = i * sizeOfBlock;
            if (offsets.contains(offset)) {
                DataBlock block = loadOverFillBlock(offset, overFillBlockFactor);
                list.add(block.getBString(offset));
            } else {
                list.add(new Block_String(Long.toString(offset), "FREE BLOCK", "FREE BLOCK", "FREE BLOCK", (new ArrayList<String>())));
            }

        }
        return list;
    }

    /**
     *  výpis prepojenia dátových a preplnujúcich blokov
     *
     * @return
     * @throws IOException
     */
    public List<List<Block_String>> getAllBlocksStrings() throws IOException {
        List<ExternalNode> nodes = getInOrder();

        List<List<Block_String>> lists = new ArrayList<>(nodes.size());

        for (ExternalNode node : nodes) {
            List<DataBlock> chain = getOverFillChain(loadDataBlock(node.getFileOffset(), dataBlockFactor));
            List<Block_String> list = new ArrayList<>(chain.size());
            long currentOffset = node.getFileOffset();
            for (DataBlock dataBlock : chain) {
                list.add(dataBlock.getBString(currentOffset));

                currentOffset = dataBlock.getOverFill_Offset();
            }
            lists.add(list);
        }

        return lists;

    }

    /**
     * Nájde record dynamickom hashoaní
     *
     * @param <T>
     * @param record record s kľúčovými atribútmi
     * @return nájdený záznam
     * @throws IOException
     */
    public <T extends Record> T find(Record record) throws IOException {

        ExternalNode node = (ExternalNode) getNode(record.getHash()).node;

        if (node.getFileOffset() == -1) {
            return null;
        }

        DataBlock block = loadDataBlock(node.getFileOffset(), dataBlockFactor);

        while (true) {
            Record searched = block.getRecord(record);
            if (searched != null) {
                return (T) searched;
            } else if (block.getOverFill_Offset() != -1) {
                block = loadOverFillBlock(block.getOverFill_Offset(), overFillBlockFactor);
            } else {
                break;
            }
        }

        return null;

    }

    /**
     * Načíta celé zreťazenie dátového bloku, LEN NA TESTING a na výpis
     *
     * @param block
     * @return
     * @throws IOException
     */
    public ArrayList<DataBlock> getOverFillChain(DataBlock block) throws IOException {
        ArrayList<DataBlock> blocks = new ArrayList<>();
        blocks.add(block);

        while (true) {

            if (block.getOverFill_Offset() == -1) {
                break;
            } else {
                block = loadOverFillBlock(block.getOverFill_Offset(), overFillBlockFactor);
            }
            blocks.add(block);
        }
        return blocks;
    }

    public int validCount() {
        ArrayList<ExternalNode> arr = getInOrder();
        return arr.stream().mapToInt(ExternalNode::getValidCount).sum();

    }

    /**
     * Zmaže prvok z dynamického hashovania
     *
     * @param record
     * @return true ak prvok bol zmazaný
     * @throws IOException
     */
    public boolean delete(Record record) throws IOException {
        ExternalNode node = (ExternalNode) getNode(record.getHash()).node;
        // Prvok neexistuje v hashingu
        if (node.getFileOffset() == -1) {
            return false;
        }
        long dataBlockOffset = node.getFileOffset();
        long currentOverFillOffset = dataBlockOffset;
        long previousOffset = -1;
        // Načítaj dátový blok
        DataBlock block = loadDataBlock(node.getFileOffset(), dataBlockFactor);
        DataBlock dataBlock = block;
        DataBlock previousBlock = null;
        while (true) {
            int searchedIndex = block.getRecordIndex(record);
            // Záznam nájdený
            if (searchedIndex != -1) {
                block.deleteAtIndex(searchedIndex);
                node.subtractFromCount(1);

                // Mazalo sa z dátového bloku
                if (dataBlock == block) {
                    //Dátový blok ostal prázdný
                    if (block.getValidCount() == 0) {
                        // Uvolním dátový blok, ak nemá zreťazenie
                        if (block.getOverFill_Offset() == -1) {
                            node.setFileOffset(-1);
                            dataFileManager.addFreeBlock(dataBlockOffset);
                            //Dátový blok prázdny ale má zreťazenie
                        } else {
                            writeDataBlock(dataBlockOffset, block);
                            return true;
                        }

                        // Mazal som z dátového ale neuvolnil sa celý, pozri či je možné bloky zlúčiť
                    } else // Nemá chain, skúsim cyklicky zlúčiť s bratom
                     if (block.getOverFill_ChainLength() == 0) {
                            NodeBlockPair pair = checkNodesJoin(node, block);                        
                            dataBlockOffset = pair.node.getFileOffset();
                            block = pair.block;
                            writeDataBlock(dataBlockOffset, block);
                            return true;
                            // Má zreťazenie, skús zlúčiť bloky v rámci zreťazenia
                        } else {
                            writeDataBlock(dataBlockOffset, block);
                            joinOnBlocks(dataBlock, node);
                                return true;
                            

                        }

                    // Mazali sme z preplnujúceho bloku
                } else // Preplnujúci blok je prázdny
                if (block.getValidCount() == 0) {
                    // Predchádzajúci je dátový v zreťazení
                    if (previousBlock == dataBlock) {
                        dataBlock.setOverFill_ChainLength(dataBlock.getOverFill_ChainLength() - 1);
                        // Prelikovanie predchádzajúceho a dalsieho
                        dataBlock.setOverFill_Offset(block.getOverFill_Offset());
                        writeDataBlock(dataBlockOffset, dataBlock);
                        // pridaj currentOffset do volnych blokov
                        overFillFileManager.addFreeBlock(currentOverFillOffset);

                        return true;

                        // Predchádzajúci je tiež preplnujuci
                    } else {
                        dataBlock.setOverFill_ChainLength(dataBlock.getOverFill_ChainLength() - 1);
                        writeDataBlock(dataBlockOffset, dataBlock);
                        overFillFileManager.addFreeBlock(currentOverFillOffset);

                        previousBlock.setOverFill_Offset(block.getOverFill_Offset());
                        writeOverFillBlock(previousOffset, previousBlock);
                        return true;
                    }

                    // Mazal som z preplnujúceho ale neuvolnil sa celý, zlúč bloky ak sa dá
                } else {
                    writeOverFillBlock(currentOverFillOffset, block);
                    joinOnBlocks(dataBlock, node);
                    return true;

                }
             // Prvok sa nenašiel v súčasnom bloku, načítaj další v zreťazení
            } else if (block.getOverFill_Offset() != -1) {
                previousOffset = currentOverFillOffset;
                previousBlock = block;

                currentOverFillOffset = block.getOverFill_Offset();

                block = loadOverFillBlock(currentOverFillOffset, overFillBlockFactor);
            // Prvok sa nenašiel v celom zreťazení
            } else {
               return false;
            }
        }

        

    }

    /**
     * Ak je to možné tak zlúči bloky v zreťazení
     *
     * @param dataBlock
     * @param node
     * @return true - ak boli zlúčené nejaké bloky
     * @throws IOException
     */
    private void joinOnBlocks(DataBlock dataBlock, ExternalNode node) throws IOException {
        int totalSpace = dataBlockFactor + (dataBlock.getOverFill_ChainLength() * overFillBlockFactor);
        int occupied = node.getValidCount();
        int freeSpace = totalSpace - occupied;

        // 
        DataBlock current = dataBlock;
        DataBlock previous = null;
        long offSetOfPrevious = -1;
        // podmienka ušetrenia aspoň jedného bloku
        if (freeSpace >= overFillBlockFactor) {

            dataBlock.setOverFill_ChainLength(dataBlock.getOverFill_ChainLength() - 1);
            writeDataBlock(node.getFileOffset(), dataBlock);

            long offsetOfCurrent = node.getFileOffset();
            while (current.getOverFill_Offset() != -1) {

                DataBlock next = loadOverFillBlock(current.getOverFill_Offset(), overFillBlockFactor);

                if (current == dataBlock) {
                    if (dataBlock.getValidCount() < dataBlockFactor) {
                        // doplň do súčasného bloku záznamy z nasledujúceho
                        int numOfRecordsToCopy = dataBlockFactor - current.getValidCount();
                        dataBlock.addRecords(next.removeNumOfRecords(numOfRecordsToCopy));
                        writeDataBlock(node.getFileOffset(), dataBlock);
                    }

                } else if (current.getValidCount() < overFillBlockFactor) {
                    // doplň do súčasného bloku záznamy z nasledujúceho
                    int numOfRecordsToCopy = overFillBlockFactor - current.getValidCount();
                    current.addRecords(next.removeNumOfRecords(numOfRecordsToCopy));
                    writeOverFillBlock(offsetOfCurrent, current);
                }
                // načítaj další zo zreťazenia
                offSetOfPrevious = offsetOfCurrent;
                previous = current;
                offsetOfCurrent = current.getOverFill_Offset();
                current = next;

            }
           // posledný blok v zreťazení je volný
            overFillFileManager.addFreeBlock(offsetOfCurrent);
            previous.setOverFill_Offset(-1);
            if (previous == dataBlock) {
                writeDataBlock(node.getFileOffset(), dataBlock);
            } else {
                writeOverFillBlock(offSetOfPrevious, previous);
            }
            
        }

    }

    /**
     * Ak je to možné tak zlúči externé vrcholy , postupuje cyklicky od
     * externého do do jeho rodiča
     *
     * @param node
     * @param nodeBlock
     * @return blok vzniknutý cyklickým spájaním a externý vrchol ku ktorému je
     * priradený
     * @throws IOException
     */
    private NodeBlockPair checkNodesJoin(ExternalNode node, DataBlock nodeBlock) throws IOException {
        while (true) {
            if (node.parent != null) {
                boolean leftOfParent = node == node.parent.left;
                if (leftOfParent) {
                    Node rightBrother = node.parent.right;
                    if (rightBrother != null) {
                        if (rightBrother instanceof ExternalNode) {
                            ExternalNode exRightBrother = (ExternalNode) rightBrother;

                            if (exRightBrother.getValidCount() + node.getValidCount() <= dataBlockFactor) {

                                DataBlock rightBrotherBlock = null;
                                DataBlock joined = null;

                                if (exRightBrother.getFileOffset() == -1) {
                                    joined = nodeBlock;
                                } else {
                                    rightBrotherBlock = loadDataBlock(exRightBrother.getFileOffset(), dataBlockFactor);
                                    if (rightBrotherBlock.getOverFill_ChainLength() != 0) {
                                        break;
                                    }
                                    joined = joinDataBlocks(nodeBlock, rightBrotherBlock);
                                    dataFileManager.addFreeBlock(((ExternalNode) rightBrother).getFileOffset());
                                }

                                ExternalNode newParent = new ExternalNode(node.parent.parent, joined.getValidCount(), node.getFileOffset());
//                                System.out.println("LEFT BRANCH");
                                //uvolnil sa pravy block

                                // ------------------
//                                writeDataBlock(newParent.getFileOffset(), joined, dataBlockFactor);
                                if (node.parent.parent != null) {
                                    if (node.parent.parent.left == node.parent) {
                                        // parent is left of parents parent
                                        node.parent.parent.left = newParent;
                                    } else {
                                        node.parent.parent.right = newParent;
                                    }
                                } else {
                                    root = newParent;
                                }

                                node = newParent;
                                nodeBlock = joined;

                            } else {
                                break;
                            }

                        } else {
                            break;
                        }

                    } else {
                        break;
                    }
                    // Node is rightOfParent
                } else {
                    Node leftBrother = node.parent.left;
                    if (leftBrother != null) {
                        if (leftBrother instanceof ExternalNode) {
                            ExternalNode exLeftBrother = (ExternalNode) leftBrother;
                            if (exLeftBrother.getValidCount() + node.getValidCount() <= dataBlockFactor) {

                                DataBlock leftBrotherBlock = null;
                                DataBlock joined = null;
                                if (exLeftBrother.getFileOffset() == -1) {
                                    joined = nodeBlock;
                                } else {
                                    leftBrotherBlock = loadDataBlock(exLeftBrother.getFileOffset(), dataBlockFactor);
                                    if (leftBrotherBlock.getOverFill_ChainLength() != 0) {
                                        break;
                                    }
                                    joined = joinDataBlocks(nodeBlock, leftBrotherBlock);
                                    dataFileManager.addFreeBlock(((ExternalNode) leftBrother).getFileOffset());
                                }

                                ExternalNode newParent = new ExternalNode(node.parent.parent, joined.getValidCount(), node.getFileOffset());
//                                 System.out.println("RIGHT BRANCH");
                                //uvolnil sa lavy block

                                // ------------------
//                                writeDataBlock(newParent.getFileOffset(), joined, dataBlockFactor);
                                if (node.parent.parent != null) {
                                    if (node.parent.parent.left == node.parent) {
                                        node.parent.parent.left = newParent;
                                    } else {
                                        node.parent.parent.right = newParent;
                                    }

                                } else {
                                    root = newParent;
                                }

                                node = newParent;
                                nodeBlock = joined;

                            } else {
                                break;
                            }

                        } else {
                            break;
                        }

                    } else {
                        break;
                    }

                }

            } else {
                break;
            }

        }
        return new NodeBlockPair(node, nodeBlock);
    }

    /**
     * Spojí bloky do jedného
     *
     * @param first
     * @param second
     * @return
     */
    private DataBlock joinDataBlocks(DataBlock first, DataBlock second) {
        DataBlock newBlock = new DataBlock(dataBlockFactor);
        for (Record record : first.getValidRecords()) {
            newBlock.addRecord(record);
        }
        for (Record record : second.getValidRecords()) {
            newBlock.addRecord(record);
        }
        return newBlock;

    }

    /**
     * Na základe hashu vráti externú nodu a jej hľbku, w ktorej by mal daný
     * prvok byť
     *
     * @param hash
     * @return
     */
    private ReturnNode_Depth getNode(BitSet hash) {

        Node current = root;
        int hashBit = maxHashBit;
        int depth = 0;

        while (current != null) {

            if (current instanceof ExternalNode) {
                return new ReturnNode_Depth(current, depth);
            }

            if (hash.get(hashBit)) {
                depth++;
                current = current.right;
            } else {
                depth++;
                current = current.left;
            }

            hashBit--;

        }

        return null;
    }

    /**
     * Prehliadka Trie-U, kwôli výpisu
     *
     * @return
     */
    public ArrayList<ExternalNode> getInOrder() {
        ArrayList<ExternalNode> arr = new ArrayList<>();
        if (root == null) {

            return arr;
        }

        Stack<Node> stack = new Stack<>();
        Node current = root;
        while (current != null || stack.size() > 0) {

            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            if (current instanceof ExternalNode) {
                if (((ExternalNode) current).getFileOffset() != -1) {
                    arr.add((ExternalNode) current);
                }
            }
            current = current.right;
        }

        return arr;

    }

    public String toString_Blocks() throws IOException {
        ArrayList<ExternalNode> nodes = getInOrder();
        String ret = "";
        for (ExternalNode node : nodes) {
            DataBlock dataBlock = loadDataBlock(node.getFileOffset(), dataBlockFactor);
            ArrayList<DataBlock> chain = getOverFillChain(dataBlock);
            ret += "***********************" + "**********************\n";
            for (DataBlock block : chain) {
                if (block == dataBlock) {
                    ret += "----- DATA BLOCK at offset: " + node.getFileOffset() + " number of valid records: " + node.getValidCount() + " ------\n";
                    ret += block.toString_wChain();
                    ret += "-------------------------------------------\n";
                } else {
                    ret += block.toString_noChain();
                }

            }
            ret += "************************************************************\n\n";

        }
        return ret;

    }

    private void printTree(Node root, int level) {
        if (root == null) {
            return;
        }
        printTree(root.right, level + 1);
        if (level != 0) {
            for (int i = 0; i < level - 1; i++) {
                System.out.print("|\t");
            }
            if (root instanceof ExternalNode) {
                System.out.println("|------- " + "E " + ((ExternalNode) root).getFileOffset() + " | " + ((ExternalNode) root).getValidCount());
            } else {
                System.out.println("|------- " + "I");
            }
        } else if (root instanceof ExternalNode) {
            System.out.println("E " + ((ExternalNode) root).getFileOffset() + " | " + ((ExternalNode) root).getValidCount());
        } else {
            System.out.println("I");
        }
        printTree(root.left, level + 1);
    }

    public void printTree() {

        printTree(root, 0);

    }

    /* ******************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private void writeDataBlock(long offset, DataBlock block) throws IOException {
        Utilities.writeBytesToFile(dataFile, offset, block.toByteArray(exampleInstance.getClass()));
    }

    private void writeOverFillBlock(long offset, DataBlock block) throws IOException {
        Utilities.writeBytesToFile(overFillFile, offset, block.toByteArray(exampleInstance.getClass()));
    }

    private DataBlock loadDataBlock(long offset, int blockFactor) throws IOException {
        return DataBlock.fromByteArray(Utilities.readBytesFromFile(dataFile, offset, DataBlock.getByteArraySize(blockFactor, exampleInstance.getClass())), blockFactor, exampleInstance.getClass());
    }

    private DataBlock loadOverFillBlock(long offset, int blockFactor) throws IOException {
        return DataBlock.fromByteArray(Utilities.readBytesFromFile(overFillFile, offset, DataBlock.getByteArraySize(blockFactor, exampleInstance.getClass())), blockFactor, exampleInstance.getClass());
    }

    /*
    //***************************************************************************************************************
     */
    public void saveState(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;

        writer = new PrintWriter(fileName, "UTF-8");
        writer.println(dataBlockFactor + ";" + overFillBlockFactor + ";" + maxHashBit + ";");
        ArrayList<Long> freeDataBlocks = dataFileManager.getFreeBlocks();
        for (Long freeDataBlock : freeDataBlocks) {
            writer.print(freeDataBlock + ";");

        }
        writer.println();
        ArrayList<Long> freeOverFillBlocks = overFillFileManager.getFreeBlocks();
        for (Long freeDataBlock : freeOverFillBlocks) {
            writer.print(freeDataBlock + ";");

        }
        writer.println();

        saveNode(root, writer);
        writer.close();

    }

    private void saveNode(Node node, PrintWriter writer) {

        if (node == null) {
            writer.print("-2;");
        }

        if (node instanceof ExternalNode) {
            ExternalNode ext = (ExternalNode) node;
            writer.print(ext.getFileOffset() + "/" + ext.getValidCount() + ";");
        } else {
            writer.print("-1" + ";");
            saveNode(node.left, writer);
            saveNode(node.right, writer);
        }

    }

    public static <T extends Record> Dynamic_hashing loadInstance(String fileName, String dataFileName, String overFillFileName, Class<T> recordClass) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        RandomAccessFile dataFile = new RandomAccessFile(new File(dataFileName), "rw");
        RandomAccessFile overFillFile = new RandomAccessFile(new File(overFillFileName), "rw");

        String row = reader.readLine();
        String[] tokens = row.split(";");
        int dataBlockFactor = Integer.parseInt(tokens[0]);
        int overFillBlockFactor = Integer.parseInt(tokens[1]);
        int maxHashBit = Integer.parseInt(tokens[2]);

        row = reader.readLine();
        tokens = row.split(";");

        FreeBlocksManager dataFreeBlocksManager = new FreeBlocksManager(dataFile, DataBlock.getByteArraySize(dataBlockFactor, recordClass));
        if (!row.isEmpty()) {
            for (int i = 0; i < tokens.length; i++) {
                dataFreeBlocksManager.addBlock_noCheck(Integer.parseInt(tokens[i]));
            }
        }
        row = reader.readLine();
        tokens = row.split(";");

        FreeBlocksManager overFillFreeBlockManager = new FreeBlocksManager(overFillFile, DataBlock.getByteArraySize(overFillBlockFactor, recordClass));
        if (!row.isEmpty()) {
            for (int i = 0; i < tokens.length; i++) {
                overFillFreeBlockManager.addBlock_noCheck(Integer.parseInt(tokens[i]));
            }
        }

        row = reader.readLine();
        tokens = row.split(";");
        Scanner s = new Scanner(row).useDelimiter(";");
        Node root = loadNode(s, null);

        return new Dynamic_hashing(dataFile, overFillFile, dataFreeBlocksManager, overFillFreeBlockManager, dataBlockFactor, overFillBlockFactor, maxHashBit, root, recordClass);

    }

    private static Node loadNode(Scanner s, Node parent) {

        String token = s.next();

        if (token.contains("/")) {
            String[] exTokens = token.split("/");
            ExternalNode node = new ExternalNode(parent, Integer.parseInt(exTokens[1]), Integer.parseInt(exTokens[0]));
            return node;

        } else if (Integer.parseInt(token) == -1) {
            InternalNode node = new InternalNode();
            node.setParent(parent);

            node.setLeft(loadNode(s, node));
            node.setRight(loadNode(s, node));
            return node;

        } else {
            return null;
        }

    }

}
