package FinalProject;

import java.util.HashMap;
import java.util.HashSet;

class SymbolTable {
    private Node root;
    private Node current;

    public SymbolTable() {
        root = new Node(null);
        current = root;
    }

    public void add(String id, Symbol s) {
        current.addSymbol(id, s);
    }

    public void incScope() {
        Node next = new Node(current);
        current.addChild(next);
        current = next;
    }

    public void decScope() {
        if (current.getParent() != null) {
            current = current.getParent();
        }
    }

    public boolean contains(String id) {
        boolean b = false;
        for (Node node = current;
                current != null && !b;
                node = node.getParent()) {
            b = node.contains(id);
        }
        return b;
    }

    public Symbol getSymbol(String id) {
        Symbol s = null;
        for (Node node = current;
                node != null && s == null;
                node = node.getParent()) {
            s = node.getSymbol(id);
        }
        return s;
    }

    private class Node {
        private Node parent;
        private HashMap<String, Symbol> symbols;
        private HashSet<Node> children;

        public Node(Node parent) {
            this.parent = parent;
            symbols = new HashMap<>();
            children = new HashSet<>();
        }

        public Node getParent() {
            return parent;
        }

        public boolean contains(String id) {
            return symbols.containsKey(id);
        }

        public void addSymbol(String id, Symbol s) {
            symbols.put(id, s);
        }

        public Symbol getSymbol(String id) {
            return symbols.get(id);
        }

        public HashMap<String, Symbol> getSymbols() {
            return symbols;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public HashSet<Node> getChildren() {
            return children;
        }
    }
}
