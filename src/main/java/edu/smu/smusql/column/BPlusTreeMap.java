package edu.smu.smusql.column;

import java.util.*;

public class BPlusTreeMap<K extends Comparable<K>, V> extends AbstractMap<K, V> implements SortedMap<K, V> {
    private BPlusTreeNode root;
    private final int order;

    // B+ Tree Node class to represent internal and leaf nodes
    private class BPlusTreeNode {
        boolean isLeaf;
        List<K> keys;
        List<BPlusTreeNode> children;
        List<V> values;  // Stores values if the node is a leaf
        BPlusTreeNode next;

        BPlusTreeNode(boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.values = isLeaf ? new ArrayList<>() : null;
            this.next = null;
        }
    }

    // Constructor to initialize the B+ Tree Map with a specified order
    public BPlusTreeMap(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }

    // Find the appropriate leaf node for insertion
    private BPlusTreeNode findLeaf(K key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.compareTo(node.keys.get(i)) >= 0) {
                i++;
            }
            node = node.children.get(i);
        }
        return node;
    }

    // Insert a key-value pair into the B+ Tree Map
    public V put(K key, V value) {
        BPlusTreeNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);

        if (pos >= 0) {  // Key already exists, update the value
            V oldValue = leaf.values.set(pos, value);
            return oldValue;
        }

        pos = -(pos + 1);  // Position where the key should be inserted
        leaf.keys.add(pos, key);
        leaf.values.add(pos, value);

        // Split the leaf node if it exceeds the order
        if (leaf.keys.size() > order - 1) {
            splitLeaf(leaf);
        }
        return null;
    }

    private void splitLeaf(BPlusTreeNode leaf) {
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);
        int mid = (leaf.keys.size()) / 2;
        
        // Copy second half to new leaf
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.values.addAll(leaf.values.subList(mid, leaf.values.size()));
        
        // Remove second half from original leaf
        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.values.subList(mid, leaf.values.size()).clear();
        
        // Link the leaves
        newLeaf.next = leaf.next;
        leaf.next = newLeaf;
        
        K splitKey = newLeaf.keys.get(0); // Use first key of new leaf as split key
        
        if (leaf == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(splitKey);
            newRoot.children.add(leaf);
            newRoot.children.add(newLeaf);
            root = newRoot;
        } else {
            insertIntoParent(leaf, splitKey, newLeaf);
        }
    }
    
    private void insertIntoParent(BPlusTreeNode left, K key, BPlusTreeNode right) {
        BPlusTreeNode parent = findParent(root, left);
        
        if (parent == null) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            root = newRoot;
            return;
        }
        
        int insertPos = 0;
        while (insertPos < parent.keys.size() && parent.keys.get(insertPos).compareTo(key) < 0) {
            insertPos++;
        }
        
        parent.keys.add(insertPos, key);
        parent.children.add(insertPos + 1, right);
        
        if (parent.keys.size() > order - 1) {
            splitInternal(parent);
        }
    }
    
    private void splitInternal(BPlusTreeNode internal) {
        BPlusTreeNode newInternal = new BPlusTreeNode(false);
        int mid = (internal.keys.size()) / 2;
        K midKey = internal.keys.get(mid);
        
        // Copy keys and children after mid to new node
        newInternal.keys.addAll(internal.keys.subList(mid + 1, internal.keys.size()));
        newInternal.children.addAll(internal.children.subList(mid + 1, internal.children.size()));
        
        // Remove them from original node
        internal.keys.subList(mid, internal.keys.size()).clear();
        internal.children.subList(mid + 1, internal.children.size()).clear();
        
        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(midKey);
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            insertIntoParent(internal, midKey, newInternal);
        }
    }
    
    private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode target) {
        if (current.isLeaf) {
            return null;
        }
        
        if (current.children.contains(target)) {
            return current;
        }
        
        for (BPlusTreeNode child : current.children) {
            if (!child.isLeaf) {
                BPlusTreeNode result = findParent(child, target);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    public V get(Object key) {
        BPlusTreeNode leaf = findLeaf((K) key);
        int pos = Collections.binarySearch(leaf.keys, (K) key);
        return pos >= 0 ? leaf.values.get(pos) : null;
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new LinkedHashSet<>();
        BPlusTreeNode node = findFirstLeaf();
        while (node != null) {
            for (int i = 0; i < node.keys.size(); i++) {
                entries.add(new AbstractMap.SimpleEntry<>(node.keys.get(i), node.values.get(i)));
            }
            node = node.next;
        }
        return entries;
    }

    private BPlusTreeNode findFirstLeaf() {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            node = node.children.get(0);
        }
        return node;
    }

    @Override
    public Comparator<? super K> comparator() {
        return null; // Natural ordering
    }

    //not implemented
    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return null;
    }

    // not implemented
    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return null;
    }

    // not implemented
    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return null;
    }

    public SortedMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new SubMap(this, fromKey, fromInclusive, toKey, toInclusive);
    }

    public SortedMap<K, V> headMap(K toKey, boolean inclusive) {
        return new SubMap(this, null, true, toKey, inclusive);
    }

    public SortedMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return new SubMap(this, fromKey, inclusive, null, true);
    }

    @Override
    public K firstKey() {
        BPlusTreeNode node = findFirstLeaf();
        return node.keys.isEmpty() ? null : node.keys.get(0);
    }

    @Override
    public K lastKey() {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            node = node.children.get(node.children.size() - 1);
        }
        return node.keys.isEmpty() ? null : node.keys.get(node.keys.size() - 1);
    }

    // SubMap class to handle subMap, headMap, and tailMap views
    private class SubMap extends AbstractMap<K, V> implements SortedMap<K, V> {
        private final BPlusTreeMap<K, V> map;
        private final K fromKey;
        private final boolean fromInclusive;
        private final K toKey;
        private final boolean toInclusive;

        SubMap(BPlusTreeMap<K, V> map, K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            this.map = map;
            this.fromKey = fromKey;
            this.fromInclusive = fromInclusive;
            this.toKey = toKey;
            this.toInclusive = toInclusive;
        }

        @Override
        public Comparator<? super K> comparator() {
            return map.comparator();
        }

        @Override
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return new SubMap(map, fromKey, true, toKey, false);
        }

        @Override
        public SortedMap<K, V> headMap(K toKey) {
            return new SubMap(map, fromKey, fromInclusive, toKey, true);
        }

        @Override
        public SortedMap<K, V> tailMap(K fromKey) {
            return new SubMap(map, fromKey, true, toKey, toInclusive);
        }

        @Override
        public K firstKey() {
            for (K key : keySet()) {
                return key;
            }
            throw new NoSuchElementException();
        }

        @Override
        public K lastKey() {
            K lastKey = null;
            for (K key : keySet()) {
                lastKey = key;
            }
            if (lastKey == null) {
                throw new NoSuchElementException();
            }
            return lastKey;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> entries = new LinkedHashSet<>();
            BPlusTreeNode node = map.findFirstLeaf();
            while (node != null) {
                for (int i = 0; i < node.keys.size(); i++) {
                    K key = node.keys.get(i);
                    boolean fromKeyCondition = fromKey == null || (fromInclusive ? key.compareTo(fromKey) >= 0 : key.compareTo(fromKey) > 0);
                    boolean toKeyCondition = toKey == null || (toInclusive ? key.compareTo(toKey) <= 0 : key.compareTo(toKey) < 0);
                    if (fromKeyCondition && toKeyCondition) {
                        entries.add(new AbstractMap.SimpleEntry<>(key, node.values.get(i)));
                    }
                }
                node = node.next;
            }
            return entries;
        }
    }

    // for testing
    public void printKeys() {
        BPlusTreeNode node = findFirstLeaf();
        while (node != null) {
            System.out.println("Leaf node keys: " + node.keys);
            node = node.next;
        }
    }

    public void printTree() {
        printNode(root, 0);
    }

    private void printNode(BPlusTreeNode node, int level) {
        if (node.isLeaf) {
            System.out.println("Level " + level + " (Leaf): " + node.keys);
        } else {
            System.out.println("Level " + level + " (Internal): " + node.keys);
            for (BPlusTreeNode child : node.children) {
                printNode(child, level + 1);
            }
        }
    }
    

    // testing
    // javac -d bin src/main/java/edu/smu/smusql/column/BPlusTreeMap.java 
    // java -cp bin edu.smu.smusql.column.BPlusTreeMap
    public static void main(String[] args) {
        BPlusTreeMap<String, String> treeMap = new BPlusTreeMap<>(3);

        // Insert some key-value pairs
        treeMap.put("b", "two");
        treeMap.put("e", "five");
        treeMap.put("h", "eight");
        treeMap.put("f", "six");
        treeMap.put("i", "nine");
        treeMap.put("d", "four");
        treeMap.put("c", "three");
        treeMap.put("j", "ten");
        treeMap.put("a", "one");
        treeMap.put("g", "seven");


        treeMap.printKeys();
        treeMap.printTree();

        // Test headMap, tailMap, and subMap
        SortedMap<String, String> headMap = treeMap.headMap("c", false);
        System.out.println("HeadMap (to 2): " + headMap);

        SortedMap<String, String> headMapInclusive = treeMap.headMap("c", true);
        System.out.println("HeadMap (to 3): " + headMapInclusive);

        SortedMap<String, String> tailMap = treeMap.tailMap("c", true);
        System.out.println("TailMap (from 3): " + tailMap);

        SortedMap<String, String> tailMapExclusive = treeMap.tailMap("c", false);
        System.out.println("TailMap (from 4): " + tailMapExclusive);
    }
}
