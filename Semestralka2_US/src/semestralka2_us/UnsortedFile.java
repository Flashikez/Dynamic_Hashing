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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Neutriedený súbor, neblokovaný, adresovaný je každý záznam
 * @author MarekPC
 */
public class UnsortedFile {

    private RandomAccessFile file;
    private int recordSize;
    private FreeBlocksManager manager;
    private Record exampleInstance;

    public <T extends Record> UnsortedFile(Class<T> clazz, String fileName) throws FileNotFoundException, IOException {
        try {
            this.exampleInstance = clazz.newInstance();
            this.recordSize = exampleInstance.getByteArraySize();
        } catch (InstantiationException ex) {

            System.err.println("Add empty construcor as first constructor declaration!");
            Logger.getLogger(UnsortedFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.err.println("Add empty construcor as first constructor declaration!");
            Logger.getLogger(UnsortedFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        freshStart(fileName);

    }

    public UnsortedFile(RandomAccessFile file, int recordSize, FreeBlocksManager manager, Record exampleInstance) {
        this.file = file;
        this.recordSize = recordSize;
        this.manager = manager;
        this.exampleInstance = exampleInstance;
    }

    public long getFileSize() throws IOException {
        return file.length();
    }

    private void freshStart(String fileName) throws FileNotFoundException, IOException {
        file = new RandomAccessFile(fileName, "rw");
        manager = new FreeBlocksManager(file, recordSize);
        file.setLength(0);
    }

    public List<Block_String> getStrings() throws IOException {

        int size = exampleInstance.getByteArraySize();
        List<Block_String> list = new ArrayList<>();
        for (int i = 0; i < file.length() / size; i++) {
            long offset = i * size;
            if (manager.getFreeBlocks().contains(offset)) {
                list.add(new Block_String(Long.toString(offset), "EMPTY","NONE","NONE", new ArrayList<String>()));

            } else {
                ArrayList<String> st = new ArrayList<>();
                st.add(loadRecord(offset).toString());

                list.add(new Block_String(Long.toString(offset), "-","--","---", st));
            }

        }
        return list;
    }

    public <T extends Record> T findFromOffset(long offset) throws IOException {

        return (T) loadRecord(offset);

    }

    public void deleteFromOffset(long offset) throws IOException {
        manager.addFreeBlock(offset);

    }

    public long insert(Record record) throws IOException {
        long offset = manager.getFreeBlock();
        if (offset == -1) {
            offset = file.length();
        }
        writeRecord(record, offset);
        return offset;

    }

    private void writeRecord(Record record, long offset) throws IOException {
        Utilities.writeBytesToFile(file, offset, record.toByteArray());

    }

    private Record loadRecord(long offset) throws IOException {
        byte[] bytes = Utilities.readBytesFromFile(file, offset, exampleInstance.getByteArraySize());
        return exampleInstance.fromByteArray(bytes);
    }

    public void saveState(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;

        writer = new PrintWriter(fileName, "UTF-8");
        ArrayList<Long> freeDataBlocks = manager.getFreeBlocks();
        for (Long freeDataBlock : freeDataBlocks) {
            writer.print(freeDataBlock + ";");

        }
        writer.close();

    }

    public static <T extends Record> UnsortedFile loadState(String saveFileName, String dataFileName, Class<T> recordClass) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        BufferedReader reader = new BufferedReader(new FileReader(saveFileName));
        RandomAccessFile dataFile = new RandomAccessFile(new File(dataFileName), "rw");

        Record exampleInstance = recordClass.newInstance();
        int recordSize = exampleInstance.getByteArraySize();
        FreeBlocksManager dataFreeBlocksManager = new FreeBlocksManager(dataFile, recordSize);
        String row = reader.readLine();

        if (row!= null && !row.isEmpty()  ) {
            String[] tokens = row.split(";");

            for (int i = 0; i < tokens.length; i++) {
                dataFreeBlocksManager.addBlock_noCheck(Integer.parseInt(tokens[i]));
            }
        }
        return new UnsortedFile(dataFile, recordSize, dataFreeBlocksManager, exampleInstance);

    }

}
