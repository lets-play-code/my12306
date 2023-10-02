import { render } from "./test-utils";
import { fireEvent } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Pagination from "../../src/components/Pagination.vue";

describe("Pagination", () => {

  it("renders correctly", async () => {
    const { container, unmount } = render(Pagination, {
      props: {
        pageNum: 1,
        total: 20,
        pageSize: 5
      }
    });

    expect(container).toMatchSnapshot();
    unmount();
  });

  it("disables previous button on first page", async () => {
    const { getByText, emitted } = render(Pagination, {
      props: {
        pageNum: 1,
        total: 20,
        pageSize: 5
      }
    });

    const previousButton = getByText("Previous");
    expect(previousButton).toHaveProperty("disabled");
    await fireEvent.click(previousButton);
    expect(emitted().updatePage).toBeFalsy();
  });

  it("disables next button on last page", async () => {
    const { getByText, emitted } = render(Pagination, {
      props: {
        pageNum: 4,
        total: 20,
        pageSize: 5
      }
    });

    const nextButton = getByText("Next");
    expect(nextButton).toHaveProperty("disabled");
    await fireEvent.click(nextButton);
    expect(emitted().updatePage).toBeFalsy();
  });

  it("updates page when next button is clicked", async () => {
    const { getByText, emitted } = render(Pagination, {
      props: {
        pageNum: 1,
        total: 20,
        pageSize: 5
      }
    });

    const nextButton = getByText("Next");
    await fireEvent.click(nextButton);

    expect(emitted().updatePage).toBeTruthy();
    expect(emitted().updatePage[0]).toEqual([2]);
  });

  it("updates page when previous button is clicked", async () => {
    const { getByText, emitted } = render(Pagination, {
      props: {
        pageNum: 2,
        total: 20,
        pageSize: 5
      }
    });

    const previousButton = getByText("Previous");
    await fireEvent.click(previousButton);

    expect(emitted().updatePage).toBeTruthy();
    expect(emitted().updatePage[0]).toEqual([1]);
  });

  it("updates page when specific page is clicked", async () => {
    const { getByRole, emitted } = render(Pagination, {
      props: {
        pageNum: 1,
        total: 20,
        pageSize: 5
      }
    });

    const pageButton = getByRole("button", { exact: false, name: /^2$/i });
    console.log(pageButton.outerHTML);
    await fireEvent.click(pageButton);

    expect(emitted().updatePage).toBeTruthy();
    expect(emitted().updatePage[0]).toEqual([2]);
  });

  it("updates page when jump to specified page by input page number", async () => {
    const { getByRole, getByTestId, emitted } = render(Pagination, {
      props: {
        pageNum: 1,
        total: 20,
        pageSize: 5
      }
    });

    const input = getByTestId("jump-page-input");
    const button = getByRole("button", { name: "Go" });

    await fireEvent.update(input, "2");
    await fireEvent.click(button);

    expect(emitted().updatePage).toBeTruthy();
    expect(emitted().updatePage[0]).toEqual([2]);
  });
});
