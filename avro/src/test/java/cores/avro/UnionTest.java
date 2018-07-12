package cores.avro;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;

public class UnionTest {
    private static void buildTest(String path, Schema s) throws IOException {
        BatchAvroColumnWriter<Record> writer = new BatchAvroColumnWriter<Record>(s, path, 1, 4, "null");
        Record r1 = new Record(s);
        r1.put(0, 1);
        r1.put(1, "s1");
        r1.put(2, (float) 0.1);
        writer.append(r1);

        Record r2 = new Record(s);
        r2.put(1, "s2");
        r2.put(2, (float) 0.2);
        writer.append(r2);

        Record r3 = new Record(s);
        r3.put(0, 3);
        r3.put(2, (float) 0.3);
        writer.append(r3);

        Record r4 = new Record(s);
        r4.put(0, 4);
        r4.put(1, "s4");
        writer.append(r4);

        Record r5 = new Record(s);
        r5.put(2, (float) 0.5);
        writer.append(r5);

        Record r6 = new Record(s);
        r6.put(0, 6);
        writer.append(r6);

        Record r7 = new Record(s);
        r7.put(1, "s7");
        writer.append(r7);

        Record r8 = new Record(s);
        writer.append(r8);

        Record r9 = new Record(s);
        r9.put(0, 9);
        r9.put(1, "s9");
        r9.put(2, (float) 0.9);
        writer.append(r9);

        Record r10 = new Record(s);
        r10.put(1, "s10");
        r10.put(2, (float) 1.0);
        writer.append(r10);

        Record r11 = new Record(s);
        r11.put(0, 11);
        r11.put(2, (float) 1.1);
        writer.append(r11);

        //dup

        Record r12 = new Record(s);
        r12.put(1, "s7");
        writer.append(r12);

        Record r13 = new Record(s);
        writer.append(r13);

        Record r14 = new Record(s);
        r14.put(0, 9);
        r14.put(1, "s9");
        r14.put(2, (float) 0.9);
        writer.append(r14);

        Record r15 = new Record(s);
        r15.put(1, "s10");
        r15.put(2, (float) 1.0);
        writer.append(r15);

        Record r16 = new Record(s);
        r16.put(0, 11);
        r16.put(2, (float) 1.1);
        writer.append(r16);

        @SuppressWarnings("unused")
        int index = writer.flush();

        File[] files = new File[2];
        for (int i = 0; i < 2; i++) {
            files[i] = new File(path + "file" + String.valueOf(i) + ".neci");
        }

        writer.mergeFiles(files);
    }

    private static void scanTest(String path, Schema s) throws IOException {
        BatchColumnReader<Record> reader = new BatchColumnReader<Record>(new File(path));
        reader.createSchema(s);
        reader.create();
        System.out.println("row count:" + reader.getRowCount(2) + " 0: " + reader.getType(0).toString() + " 1: "
                + reader.getType(1).toString() + " 2: " + reader.getType(2) + " t2: " + reader.getColumnNO("intUnion"));
        /*while (reader.hasNext()) {
            Record r = reader.next();
            int value = (int) r.get(0);
            System.out.println(value);
        }*/
        reader.close();
    }

    public static void main(String[] args) throws IOException {
        String path = "./src/test/resources/cores.avro/result/";
        Schema s = new Schema.Parser().parse(new File("./src/test/resources/cores.avro/unionTest.avsc"));
        buildTest(path, s);
        scanTest(path + "/result.neci", s);
    }
}
