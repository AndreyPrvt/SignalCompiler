import java.util.ArrayList;

public class Node{
    ArrayList<Node> children = new ArrayList<Node>();
    String type;
    String atribut;
    Node(ArrayList<Node> children, String type, String atribut){
        this.children = children;
        this.type = type;
        this.atribut = atribut;
    }

    Node(Node nd){
        this.children = nd.children;
        this.type = nd.type;
        this.atribut = nd.atribut;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void PrintTree(){
        //if (this)
        System.out.println(Tab.tab + "Type: " + this.type + " Atrib: " + this.atribut);
        System.out.println(Tab.tab + "-----------------------------------------------");
        if (this.children == null)
            return;
        System.out.println(Tab.tab + "Children: ");
        Tab.tab.append("\t\t");
        for (Node node: this.children) {
            node.PrintTree();
        }
        Tab.tab.delete(Tab.tab.length() - 3, Tab.tab.length() - 1);
    }
}

class Tab{
    static StringBuffer tab = new StringBuffer("\t");
}