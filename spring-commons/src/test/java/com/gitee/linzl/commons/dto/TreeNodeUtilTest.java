package com.gitee.linzl.commons.dto;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author linzhenlie
 * @date 2019-12-25
 */
public class TreeNodeUtilTest {
    @Test
    public void test1() {
        TreeNode root = new TreeNode();

        TreeNode node11 = new TreeNode();
        node11.setNodeId("11");
        node11.setLabel("第11结点");
        node11.setNodePid("-1");

        TreeNode node111 = new TreeNode();
        node111.setNodeId("11-1");
        node111.setLabel("第11-1结点");
        node111.setNodePid("11");

        TreeNode node12 = new TreeNode();
        node12.setNodeId("12");
        node12.setLabel("第12结点");
        node12.setNodePid("-1");

        TreeNode node121 = new TreeNode();
        node121.setNodeId("12-1");
        node121.setLabel("第12-1结点");
        node121.setNodePid("12");

        TreeNode node13 = new TreeNode();
        node13.setNodeId("13");
        node13.setLabel("第13结点");
        node13.setNodePid("-1");

        List<TreeNode> treeList = new ArrayList<>();
        treeList.add(root);
        treeList.add(node11);
        treeList.add(node111);
        treeList.add(node12);
        treeList.add(node121);
        treeList.add(node13);

        System.out.println(JSON.toJSONString(TreeNodeUtil.recursionTree(treeList)));
    }

    @Test
    public void test2() {
        TreeNode root = new TreeNode();

        TreeNode node11 = new TreeNode();
        node11.setNodeId("11");
        node11.setLabel("第11结点");
        node11.setNodePid("-1");
        root.getChildren().add(node11);

        TreeNode node111 = new TreeNode();
        node111.setNodeId("11-1");
        node111.setLabel("第11-1结点");
        node111.setNodePid("11");

        TreeNode node1112 = new TreeNode();
        node1112.setNodeId("11-1-1");
        node1112.setLabel("第11-1-1结点");
        node1112.setNodePid("11-1");
        node111.getChildren().add(node1112);

        node11.getChildren().add(node111);

        TreeNode node12 = new TreeNode();
        node12.setNodeId("12");
        node12.setLabel("第12结点");
        node12.setNodePid("-1");
        root.getChildren().add(node12);

        TreeNode node121 = new TreeNode();
        node121.setNodeId("12-1");
        node121.setLabel("第12-1结点");
        node121.setNodePid("12");
        node12.getChildren().add(node121);

        TreeNode node13 = new TreeNode();
        node13.setNodeId("13");
        node13.setLabel("第13结点");
        node13.setNodePid("-1");
        root.getChildren().add(node13);

        System.out.println(JSON.toJSONString(TreeNodeUtil.reductionTree(root)));
    }

    @Test
    public void test3() {
        TreeNode root = new TreeNode();
        root.setNodeId("1");
        root.setLabel("1-lable");

        TreeNode node11 = new TreeNode();
        node11.setNodeId("11");
        node11.setLabel("第11结点");

        TreeNode node111 = new TreeNode();
        node111.setNodeId("11-1");
        node111.setLabel("第11-1结点");

        TreeNode node1112 = new TreeNode();
        node1112.setNodeId("11-1-1");
        node1112.setLabel("第11-1-1结点");
        node111.getChildren().add(node1112);

        node11.getChildren().add(node111);
        root.getChildren().add(node11);

        TreeNode node12 = new TreeNode();
        node12.setNodeId("12");
        node12.setLabel("第12结点");

        TreeNode node121 = new TreeNode();
        node121.setNodeId("12-1");
        node121.setLabel("第12-1结点");
        node12.getChildren().add(node121);
        root.getChildren().add(node12);

        TreeNode node13 = new TreeNode();
        node13.setNodeId("13");
        node13.setLabel("第13结点");
        root.getChildren().add(node13);

        TreeNode root2 = new TreeNode();
        root2.setNodeId("2");
        root2.setLabel("2-lable");

        TreeNode node1122 = new TreeNode();
        node1122.setNodeId("1122");
        node1122.setLabel("第node1122结点");
        root2.getChildren().add(node1122);

        TreeNode node11222 = new TreeNode();
        node11222.setNodeId("11222");
        node11222.setLabel("第node11222结点");
        node1122.getChildren().add(node11222);

        System.out.println(JSON.toJSONString(TreeNodeUtil.reductionTree(Arrays.asList(root, root2))));
    }
}
