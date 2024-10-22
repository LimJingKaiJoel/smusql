// package edu.smu.smusql.index;

// // useless class i realised we don't need to code our own node, use java's node
// public class SkipListNode {
//     K key;
//     List<V> values;
//     Node[] forward;

//     public Node(int level, K key, V value) {
//         this.key = key;
//         this.values = new ArrayList<>();
//         if (value != null) {
//             this.values.add(value);
//         }
//         this.forward = new Node[level + 1];
//     }
// }